/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.dbpedia2sqlite.db;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringEscapeUtils;

public class Resource {
    public long id;
    public String name;
    public String _abstract;

    @Override
    public String toString() {
        return "Resource [id=" + id + ", name=" + name + ", _abstract=" + _abstract + "]";
    }

    public static Resource parse(String line) throws Throwable {
        Resource res = new Resource();
        int spaceIdx = line.indexOf(' ');
        String name = line.substring(0, spaceIdx);
        int slashIdx = name.lastIndexOf('/');
        int gtIdx = name.lastIndexOf('>');
        name = name.substring(slashIdx + 1, gtIdx);
        name = URLDecoder.decode(name, "utf-8");
        name = name.replace('_', ' ');
        res.name = name;

        String _abstract = line.substring(spaceIdx + 1);
        int startQuoteIdx = _abstract.indexOf('"');
        int endQuoteIdx = _abstract.lastIndexOf('"');
        if (endQuoteIdx - startQuoteIdx == 1) {
            _abstract = "";
        } else {
            _abstract = _abstract.substring(startQuoteIdx + 1, endQuoteIdx);
            _abstract = StringEscapeUtils.unescapeJava(_abstract);
        }
        res._abstract = _abstract;


        return res;
    }

}
