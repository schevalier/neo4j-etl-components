package org.neo4j.integration.provisioning.environments;

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

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.ProcessHandle;
import org.neo4j.integration.provisioning.Server;
import org.neo4j.integration.provisioning.ServerFactory;
import org.neo4j.integration.provisioning.Script;

import static java.lang.String.format;

public class Vagrant implements ServerFactory
{
    private static final String VAGRANT_FILENAME = "vagrantfile.mysql";

    private static final String VAGRANT_FILE_TEMPLATE = "" +
            "Vagrant.configure('2') do |config|\n" +
            "  config.vm.box = '%s'\n" +
            "  %s\n" +
            "  config.vm.network 'private_network', type: 'dhcp'\n" +
            "  config.vm.provision 'shell', path: 'script'\n" +
            "  config.vm.provider 'virtualbox' do |vb|\n" +
            "    vb.memory = 2048\n" +
            "  end\n" +
            "end\n";

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
    public Server createServer( Script script ) throws Exception
    {
        Files.createDirectories( directory );

        write( directory, VAGRANT_FILENAME, vagrantFileContent() );
        write( directory, "script", script.value() );

        createServer( directory );

        return new VagrantHandle( ssh( directory, GET_IP_COMMAND ), this );
    }

    private String vagrantFileContent()
    {
        if ( boxUri == null )
        {
            return format( VAGRANT_FILE_TEMPLATE, DEFAULT_BOX, "" );
        }
        else
        {
            String boxName = new File( boxUri.getPath() ).getName();
            String boxUrlConfig = format( "config.vm.box_url = '%s'", boxUri );
            return format( VAGRANT_FILE_TEMPLATE, boxName, boxUrlConfig );
        }
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

