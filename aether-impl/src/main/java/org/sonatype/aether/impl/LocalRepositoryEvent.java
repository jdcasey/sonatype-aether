package org.sonatype.aether.impl;

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

import org.sonatype.aether.Artifact;
import org.sonatype.aether.LocalRepository;
import org.sonatype.aether.RepositorySystemSession;

/**
 * An event describing an update to the local repository.
 * 
 * @author Benjamin Bentmann
 * @see LocalRepositoryMaintainer
 */
public interface LocalRepositoryEvent
{

    /**
     * Gets the repository session from which this event originates.
     * 
     * @return The repository session, never {@code null}.
     */
    RepositorySystemSession getSession();

    /**
     * Gets the local repository which has been updated.
     * 
     * @return The local repository, never {@code null}.
     */
    LocalRepository getRepository();

    /**
     * Gets the artifact that was updated.
     * 
     * @return The artifact, never {@code null}.
     */
    Artifact getArtifact();

}
