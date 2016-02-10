package org.neo4j.integration.platforms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import org.neo4j.integration.process.Commands;
import org.neo4j.integration.process.ProcessHandle;

import static java.lang.String.format;

public class Vagrant
{
    private static final String VAGRANT_FILENAME = "Vagrantfile.mysql";
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

    public String up( File directory, String scriptContent ) throws Exception
    {
        String vagrantfileContent = constructVagrantfile();

        write( directory, VAGRANT_FILENAME, vagrantfileContent );

        write( directory, "script", scriptContent );

        up( directory );

        return ssh( directory, GET_IP_COMMAND );
    }

    private String constructVagrantfile()
    {
        return format( VAGRANT_FILE_TEMPLATE, DEFAULT_BOX, "" );
    }


    public void destroy( File directory ) throws Exception
    {
        vagrant( directory, "destroy", "--force" );
    }

    private String ssh( File directory, String command ) throws Exception
    {
        return vagrant( directory, "ssh", "--command", command );
    }

    private void up( File directory ) throws Exception
    {
        vagrant( directory, "up" );
    }

    private String vagrant( File directory, String... commands ) throws Exception
    {
        Map<String, String> env = new TreeMap<>();
        env.put( "VAGRANT_VAGRANTFILE", VAGRANT_FILENAME );

        ProcessHandle processHandle = Commands.builder( prepend( "vagrant", commands ) )
                .workingDirectory( directory.toPath() )
                .failOnNonZeroExitValue()
                .timeout( 30, TimeUnit.MINUTES )
                .augmentEnvironment( env )
                .build().execute();

        return processHandle.await().stdout().trim();
    }

    private String[] prepend( String first, String[] rest )
    {
        ArrayList<String> args = new ArrayList<>();
        args.add( first );
        args.addAll( Arrays.asList( rest ) );
        return args.toArray( new String[args.size()] );
    }

    private void write( File directory, String filename, String content ) throws IOException
    {
        File file = new File( directory, filename );
        FileUtils.writeStringToFile( file, content );
    }
}

