/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.helper;

import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.model.LogSummariesType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.service.PQLService;

/**
 * UI Helper Interface. Kinda a hack, need to re-look at this.
 * @author <a href="mailto:cam.james@gmail.com>Cameron James</a>
 */
public interface UserInterfaceHelper {

    /**
     * Create a Process Summary record for the Front UI display.
     * @param process       the process
     * @param branch        the process branch
     * @param pmv           the process model version
     * @param nativeType    the native type of this model
     * @param domain        The domain of this model
     * @param created       the Date create
     * @param lastUpdate    the Date Last Updated
     * @param username      the user who updated the
     * @param isPublic      Is this model public.
     * @return the created Process Summary
     */
    ProcessSummaryType createProcessSummary(Process process, ProcessBranch branch, ProcessModelVersion pmv, String nativeType,
        String domain, String created, String lastUpdate, String username, boolean isPublic);


    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param folderId the search conditions
     * @param conditions the search conditions
     * @param similarProcesses something
     * @return the list of process Summaries
     */
    ProcessSummariesType buildProcessSummaryList(Integer folderId, String conditions, ProcessVersionsType similarProcesses);

    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param userId the search conditions
     * @param folderId the search conditions
     * @param similarProcesses something
     * @return the list of process Summaries
     */
    ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses);

    /**
     * Build a page out of the list of process summaries.
     *
     * @param userId the search conditions
     * @param folderId the search conditions
     * @param pageIndex the index into the sequence of pages of results
     * @param pageSize the desired size of the page of results
     * @return the list of process Summaries on the requested page
     */
    ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, Integer pageIndex, Integer pageSize);

    LogSummariesType buildLogSummaryList(String userId, Integer folderId, Integer pageIndex, Integer pageSize);

    public void setPQLService(PQLService pqlService);
}
