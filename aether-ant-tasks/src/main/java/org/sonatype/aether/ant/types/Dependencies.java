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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

/**
 * @author Benjamin Bentmann
 */
public class Dependencies
    extends DataType
{

    private Pom pom;

    private List<Dependency> dependencies = new ArrayList<Dependency>();

    private List<Exclusion> exclusions = new ArrayList<Exclusion>();

    protected Dependencies getRef()
    {
        return (Dependencies) getCheckedRef();
    }

    public void validate( Task task )
    {
        if ( isReference() )
        {
            getRef().validate( task );
        }
        else
        {
            for ( Dependency dependency : dependencies )
            {
                dependency.validate( task );
            }
        }
    }

    public void setRefid( Reference ref )
    {
        if ( pom != null || !exclusions.isEmpty() || !dependencies.isEmpty() )
        {
            throw noChildrenAllowed();
        }
        super.setRefid( ref );
    }

    public void addPom( Pom pom )
    {
        checkChildrenAllowed();
        if ( this.pom != null )
        {
            throw new BuildException( "You must not specify multiple <pom> elements" );
        }
        this.pom = pom;
    }

    public Pom getPom()
    {
        if ( isReference() )
        {
            return getRef().getPom();
        }
        return pom;
    }

    public void setPomRef( Reference ref )
    {
        if ( pom == null )
        {
            pom = new Pom();
            pom.setProject( getProject() );
        }
        pom.setRefid( ref );
    }

    public void addDependency( Dependency dependency )
    {
        checkChildrenAllowed();
        this.dependencies.add( dependency );
    }

    public List<Dependency> getDependencies()
    {
        if ( isReference() )
        {
            return getRef().getDependencies();
        }
        return dependencies;
    }

    public void addExclusion( Exclusion exclusion )
    {
        checkChildrenAllowed();
        this.exclusions.add( exclusion );
    }

    public List<Exclusion> getExclusions()
    {
        if ( isReference() )
        {
            return getRef().getExclusions();
        }
        return exclusions;
    }

}