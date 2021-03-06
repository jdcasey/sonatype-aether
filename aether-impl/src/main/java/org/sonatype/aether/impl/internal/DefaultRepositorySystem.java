package org.sonatype.aether.impl.internal;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.Artifact;
import org.sonatype.aether.ArtifactDescriptorException;
import org.sonatype.aether.ArtifactDescriptorRequest;
import org.sonatype.aether.ArtifactDescriptorResult;
import org.sonatype.aether.ArtifactRequest;
import org.sonatype.aether.ArtifactResolutionException;
import org.sonatype.aether.ArtifactResult;
import org.sonatype.aether.CollectRequest;
import org.sonatype.aether.CollectResult;
import org.sonatype.aether.Dependency;
import org.sonatype.aether.DependencyCollectionException;
import org.sonatype.aether.DependencyFilter;
import org.sonatype.aether.DependencyNode;
import org.sonatype.aether.DeployRequest;
import org.sonatype.aether.DeployResult;
import org.sonatype.aether.DeploymentException;
import org.sonatype.aether.InstallRequest;
import org.sonatype.aether.InstallResult;
import org.sonatype.aether.InstallationException;
import org.sonatype.aether.LocalRepository;
import org.sonatype.aether.LocalRepositoryManager;
import org.sonatype.aether.MetadataRequest;
import org.sonatype.aether.MetadataResult;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.VersionRangeRequest;
import org.sonatype.aether.VersionRangeResolutionException;
import org.sonatype.aether.VersionRangeResult;
import org.sonatype.aether.VersionRequest;
import org.sonatype.aether.VersionResolutionException;
import org.sonatype.aether.VersionResult;
import org.sonatype.aether.impl.ArtifactDescriptorReader;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.Deployer;
import org.sonatype.aether.impl.Installer;
import org.sonatype.aether.impl.MetadataResolver;
import org.sonatype.aether.impl.VersionRangeResolver;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.spi.locator.Service;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.spi.log.Logger;
import org.sonatype.aether.spi.log.NullLogger;

/**
 * @author Benjamin Bentmann
 */
@Component( role = RepositorySystem.class )
public class DefaultRepositorySystem
    implements RepositorySystem, Service
{

    @Requirement
    private Logger logger = NullLogger.INSTANCE;

    @Requirement
    private VersionResolver versionResolver;

    @Requirement
    private VersionRangeResolver versionRangeResolver;

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private MetadataResolver metadataResolver;

    @Requirement
    private ArtifactDescriptorReader artifactDescriptorReader;

    @Requirement
    private DependencyCollector dependencyCollector;

    @Requirement
    private Installer installer;

    @Requirement
    private Deployer deployer;

    public void initService( ServiceLocator locator )
    {
        setLogger( locator.getService( Logger.class ) );
        setVersionResolver( locator.getService( VersionResolver.class ) );
        setVersionRangeResolver( locator.getService( VersionRangeResolver.class ) );
        setArtifactResolver( locator.getService( ArtifactResolver.class ) );
        setMetadataResolver( locator.getService( MetadataResolver.class ) );
        setArtifactDescriptorReader( locator.getService( ArtifactDescriptorReader.class ) );
        setDependencyCollector( locator.getService( DependencyCollector.class ) );
        setInstaller( locator.getService( Installer.class ) );
        setDeployer( locator.getService( Deployer.class ) );
    }

    public DefaultRepositorySystem setLogger( Logger logger )
    {
        this.logger = ( logger != null ) ? logger : NullLogger.INSTANCE;
        return this;
    }

    public DefaultRepositorySystem setVersionResolver( VersionResolver versionResolver )
    {
        if ( versionResolver == null )
        {
            throw new IllegalArgumentException( "version resolver has not been specified" );
        }
        this.versionResolver = versionResolver;
        return this;
    }

    public DefaultRepositorySystem setVersionRangeResolver( VersionRangeResolver versionRangeResolver )
    {
        if ( versionRangeResolver == null )
        {
            throw new IllegalArgumentException( "version range resolver has not been specified" );
        }
        this.versionRangeResolver = versionRangeResolver;
        return this;
    }

    public DefaultRepositorySystem setArtifactResolver( ArtifactResolver artifactResolver )
    {
        if ( artifactResolver == null )
        {
            throw new IllegalArgumentException( "artifact resolver has not been specified" );
        }
        this.artifactResolver = artifactResolver;
        return this;
    }

    public DefaultRepositorySystem setMetadataResolver( MetadataResolver metadataResolver )
    {
        if ( metadataResolver == null )
        {
            throw new IllegalArgumentException( "metadata resolver has not been specified" );
        }
        this.metadataResolver = metadataResolver;
        return this;
    }

    public DefaultRepositorySystem setArtifactDescriptorReader( ArtifactDescriptorReader artifactDescriptorReader )
    {
        if ( artifactDescriptorReader == null )
        {
            throw new IllegalArgumentException( "artifact descriptor reader has not been specified" );
        }
        this.artifactDescriptorReader = artifactDescriptorReader;
        return this;
    }

    public DefaultRepositorySystem setDependencyCollector( DependencyCollector dependencyCollector )
    {
        if ( dependencyCollector == null )
        {
            throw new IllegalArgumentException( "dependency collector has not been specified" );
        }
        this.dependencyCollector = dependencyCollector;
        return this;
    }

    public DefaultRepositorySystem setInstaller( Installer installer )
    {
        if ( installer == null )
        {
            throw new IllegalArgumentException( "installer has not been specified" );
        }
        this.installer = installer;
        return this;
    }

    public DefaultRepositorySystem setDeployer( Deployer deployer )
    {
        if ( deployer == null )
        {
            throw new IllegalArgumentException( "deployer has not been specified" );
        }
        this.deployer = deployer;
        return this;
    }

    public VersionResult resolveVersion( RepositorySystemSession session, VersionRequest request )
        throws VersionResolutionException
    {
        return versionResolver.resolveVersion( session, request );
    }

    public VersionRangeResult resolveVersionRange( RepositorySystemSession session, VersionRangeRequest request )
        throws VersionRangeResolutionException
    {
        return versionRangeResolver.resolveVersionRange( session, request );
    }

    public ArtifactDescriptorResult readArtifactDescriptor( RepositorySystemSession session,
                                                            ArtifactDescriptorRequest request )
        throws ArtifactDescriptorException
    {
        return artifactDescriptorReader.readArtifactDescriptor( session, request );
    }

    public ArtifactResult resolveArtifact( RepositorySystemSession session, ArtifactRequest request )
        throws ArtifactResolutionException
    {
        return artifactResolver.resolveArtifact( session, request );
    }

    public List<ArtifactResult> resolveArtifacts( RepositorySystemSession session,
                                                  Collection<? extends ArtifactRequest> requests )
        throws ArtifactResolutionException
    {
        return artifactResolver.resolveArtifacts( session, requests );
    }

    public List<MetadataResult> resolveMetadata( RepositorySystemSession session,
                                                 Collection<? extends MetadataRequest> requests )
    {
        return metadataResolver.resolveMetadata( session, requests );
    }

    public CollectResult collectDependencies( RepositorySystemSession session, CollectRequest request )
        throws DependencyCollectionException
    {
        return dependencyCollector.collectDependencies( session, request );
    }

    public List<ArtifactResult> resolveDependencies( RepositorySystemSession session, DependencyNode node,
                                                     DependencyFilter filter )
        throws ArtifactResolutionException
    {
        List<ArtifactRequest> requests = new ArrayList<ArtifactRequest>();
        toArtifactRequest( requests, node, filter );

        List<ArtifactResult> results = resolveArtifacts( session, requests );

        for ( ArtifactResult result : results )
        {
            Artifact artifact = result.getArtifact();
            if ( artifact != null && artifact.getFile() != null )
            {
                result.getRequest().getDependencyNode().setArtifact( artifact );
            }
        }

        return results;
    }

    private void toArtifactRequest( List<ArtifactRequest> requests, DependencyNode node, DependencyFilter filter )
    {
        Dependency dependency = node.getDependency();
        if ( dependency != null && ( filter == null || filter.accept( node ) ) )
        {
            ArtifactRequest request = new ArtifactRequest( node );
            requests.add( request );
        }

        for ( DependencyNode child : node.getChildren() )
        {
            toArtifactRequest( requests, child, filter );
        }
    }

    public List<ArtifactResult> resolveDependencies( RepositorySystemSession session, CollectRequest request,
                                                     DependencyFilter filter )
        throws DependencyCollectionException, ArtifactResolutionException
    {
        CollectResult result = collectDependencies( session, request );
        return resolveDependencies( session, result.getRoot(), filter );
    }

    public InstallResult install( RepositorySystemSession session, InstallRequest request )
        throws InstallationException
    {
        return installer.install( session, request );
    }

    public DeployResult deploy( RepositorySystemSession session, DeployRequest request )
        throws DeploymentException
    {
        return deployer.deploy( session, request );
    }

    public LocalRepositoryManager newLocalRepositoryManager( LocalRepository localRepository )
    {
        String type = localRepository.getContentType();
        File basedir = localRepository.getBasedir();

        if ( "".equals( type ) || "enhanced".equals( type ) )
        {
            return new EnhancedLocalRepositoryManager( basedir ).setLogger( logger );
        }
        else if ( "simple".equals( type ) )
        {
            return new SimpleLocalRepositoryManager( basedir ).setLogger( logger );
        }
        else
        {
            throw new IllegalArgumentException( "Invalid repository type: " + type );
        }
    }

}
