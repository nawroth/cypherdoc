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

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

public class CypherDoc
{
    static final String EOL = System.getProperty( "line.separator" );

    private CypherDoc()
    {

    }

    public static String parse( String input )
    {
        String[] lines = input.split( EOL );
        List<Block> blocks = new ArrayList<Block>();
        if ( lines.length < 3 )
        {
            throw new IllegalArgumentException( "To little content, only "
                                                + lines.length + " lines." );
        }
        List<String> currentBlock = new ArrayList<String>();
        for ( String line : lines )
        {
            if ( line.trim()
                    .isEmpty() && currentBlock.size() > 0 )
            {
                blocks.add( getBlock( currentBlock ) );
                currentBlock = new ArrayList<String>();
            }
            else
            {
                currentBlock.add( line );
            }
        }
        if ( currentBlock.size() > 0 )
        {
            blocks.add( getBlock( currentBlock ) );
        }

        StringBuilder output = new StringBuilder( 4096 );
        GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase();
        ExecutionEngine engine = new ExecutionEngine( database );
        Block previousBlock = null;
        for ( Block block : blocks )
        {
            output.append( block.process( previousBlock, engine ) );
            output.append( EOL )
                    .append( EOL );
            previousBlock = block;
        }

        return output.toString();
    }

    private static Block getBlock( List<String> lines )
    {
        for ( BlockType type : BlockType.values() )
        {
            if ( type.isA( lines ) )
            {
                return new Block( lines, type );
            }
        }
        throw new IllegalArgumentException(
                "Unidentifiable block, starting with:\n" + lines.get( 0 ) );
    }
}
