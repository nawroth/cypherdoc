/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
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
package org.neo4j.doc.cypherdoc;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;

class Block
{
    public final List<String> lines;
    public final BlockType type;

    Block( List<String> lines, BlockType type )
    {
        this.lines = lines;
        this.type = type;
    }

    String process( Block previousBlock, ExecutionEngine engine )
    {
        return type.process( this, previousBlock, engine );
    }

    @Override
    public String toString()
    {
        return "Block [[" + type.name() + "]]:" + CypherDoc.EOL
               + StringUtils.join( lines, CypherDoc.EOL ) + CypherDoc.EOL;
    }
}
