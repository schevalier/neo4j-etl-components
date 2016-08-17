package org.neo4j.etl.provisioning.platforms;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.neo4j.etl.process.Commands;
import org.neo4j.etl.process.ProcessHandle;
import org.neo4j.etl.provisioning.Script;
import org.neo4j.etl.provisioning.Server;
import org.neo4j.etl.provisioning.ServerFactory;
import org.neo4j.etl.util.Loggers;
import org.neo4j.etl.util.Strings;

import static java.lang.String.format;

public class Vagrant implements ServerFactory
{
    private static final String VAGRANT_FILENAME = "vagrantfile.mysql";

    private static final String VAGRANT_FILE_TEMPLATE = Strings.lineSeparated(
            "Vagrant.configure('2') do |config|",
            "  config.vm.box = '%s'",
            "  %s",
            "  config.vm.network 'private_network', type: 'dhcp'",
            "  config.vm.provision 'shell', path: 'script'",
            "  config.vm.provider 'virtualbox' do |vb|",
            "    vb.memory = 2048",
            "  end",
            "end" );

    private static final String DEFAULT_BOX = "ubuntu/trusty64";

    private static final String GET_IP_COMMAND =
            "ifconfig eth1 | grep 'inet addr' | cut --delimiter=' ' --fields=12 | cut --delimiter=':' --fields=2";

    private final URI boxUri;
    private final Path directory;

    public Vagrant( Path directory )
    {
        this( null, directory );
    }

    public Vagrant( URI boxUri, Path directory )
    {
        this.boxUri = boxUri;
        this.directory = directory;
    }

    @Override
    public Server createServer( Script script, TestType testType ) throws Exception
    {
        Files.createDirectories( directory );

        write( directory, VAGRANT_FILENAME, vagrantFileContent() );
        write( directory, "script", script.value() );

        createServer( directory );

        return new VagrantHandle( ssh( directory, GET_IP_COMMAND ), this );
    }

    private String vagrantFileContent()
    {
        String fileContent;
        if ( boxUri == null )
        {
            fileContent = format( VAGRANT_FILE_TEMPLATE, DEFAULT_BOX, "" );
        }
        else
        {
            String boxName = new File( boxUri.getPath() ).getName();
            String boxUrlConfig = format( "config.vm.box_url = '%s'", boxUri );
            fileContent = format( VAGRANT_FILE_TEMPLATE, boxName, boxUrlConfig );
        }

        Loggers.Default.log( Level.FINE, "Vagrantfile: {0}", fileContent );

        return fileContent;
    }

    public void destroy() throws Exception
    {
        vagrant( directory, "destroy", "--force" );
    }

    private String ssh( Path directory, String command ) throws Exception
    {
        return vagrant( directory, "ssh", "--command", command );
    }

    private void createServer( Path directory ) throws Exception
    {
        vagrant( directory, "up" );
    }

    private String vagrant( Path directory, String... commands ) throws Exception
    {
        Map<String, String> env = new TreeMap<>();
        env.put( "VAGRANT_VAGRANTFILE", VAGRANT_FILENAME );

        ProcessHandle processHandle = Commands.builder( prepend( "vagrant", commands ) )
                .workingDirectory( directory )
                .failOnNonZeroExitValue()
                .timeout( 30, TimeUnit.MINUTES )
                .augmentEnvironment( env )
                .build()
                .execute();

        return processHandle.await().stdout().trim();
    }

    private String[] prepend( String first, String[] rest )
    {
        ArrayList<String> args = new ArrayList<>();
        args.add( first );
        args.addAll( Arrays.asList( rest ) );
        return args.toArray( new String[args.size()] );
    }

    private void write( Path directory, String filename, String content ) throws IOException
    {
        Files.write( directory.resolve( filename ), content.getBytes() );
    }

    private class VagrantHandle implements Server
    {
        private final String ipAddress;
        private final Vagrant vagrant;

        public VagrantHandle( String ipAddress, Vagrant vagrant )
        {
            this.ipAddress = ipAddress;
            this.vagrant = vagrant;
        }

        @Override
        public String ipAddress()
        {
            return ipAddress;
        }

        @Override
        public void close() throws Exception
        {
            vagrant.destroy();
        }
    }
}

