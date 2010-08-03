package org.sonatype.aether.ant.types;

/*
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0, 
 * and you may not use this file except in compliance with the Apache License Version 2.0. 
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the Apache License Version 2.0 is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.sonatype.aether.ant.AntRepoSys;

/**
 * @author Benjamin Bentmann
 */
public class Proxy
    extends DataType
{

    private String host;

    private int port;

    private String type;

    private String nonProxyHosts;

    private Authentication authentication;

    @Override
    public void setProject( Project project )
    {
        super.setProject( project );

        AntRepoSys.getInstance( project ).addProxy( this );
    }

    protected Proxy getRef()
    {
        return (Proxy) getCheckedRef();
    }

    public void setRefid( Reference ref )
    {
        if ( host != null || port != 0 || type != null || nonProxyHosts != null )
        {
            throw tooManyAttributes();
        }
        if ( authentication != null )
        {
            throw noChildrenAllowed();
        }
        super.setRefid( ref );
    }

    public String getHost()
    {
        if ( isReference() )
        {
            return getRef().getHost();
        }
        return host;
    }

    public void setHost( String host )
    {
        checkAttributesAllowed();
        this.host = host;
    }

    public int getPort()
    {
        if ( isReference() )
        {
            return getRef().getPort();
        }
        return port;
    }

    public void setPort( int port )
    {
        checkAttributesAllowed();
        if ( port <= 0 || port > 0xFFFF )
        {
            throw new BuildException( "The port number must be within the range 1 - 65535" );
        }
        this.port = port;
    }

    public String getType()
    {
        if ( isReference() )
        {
            return getRef().getType();
        }
        return type;
    }

    public void setType( String type )
    {
        checkAttributesAllowed();
        this.type = type;
    }

    public String getNonProxyHosts()
    {
        if ( isReference() )
        {
            return getRef().getNonProxyHosts();
        }
        return nonProxyHosts;
    }

    public void setNonProxyHosts( String nonProxyHosts )
    {
        checkAttributesAllowed();
        this.nonProxyHosts = nonProxyHosts;
    }

    public Authentication getAuthentication()
    {
        if ( isReference() )
        {
            return getRef().getAuthentication();
        }
        return authentication;
    }

    public void addAuthentication( Authentication authentication )
    {
        checkChildrenAllowed();
        if ( this.authentication != null )
        {
            throw new BuildException( "You must not specify multiple <authentication> elements" );
        }
        this.authentication = authentication;
    }

    public void setAuthRef( Reference ref )
    {
        if ( authentication == null )
        {
            authentication = new Authentication();
            authentication.setProject( getProject() );
        }
        authentication.setRefid( ref );
    }

}