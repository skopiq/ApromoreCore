/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.search.provider;

import java.util.Collection;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.search.SearchByModelPlugin;

public interface SearchByModelPluginProvider {

    Collection<SearchByModelPlugin> listAll();
    
    SearchByModelPlugin findByName() throws PluginNotFoundException;
    
    SearchByModelPlugin findByNameAndVersion() throws PluginNotFoundException;

}
