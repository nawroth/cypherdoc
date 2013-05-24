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

import org.apache.commons.lang3.StringUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.visualization.asciidoc.AsciidocHelper;

//TODO add GRAPH, CONSOLE
enum BlockType
{
    TITLE
    {
        @Override
        boolean isA( List<String> block )
        {
            int size = block.size();
            return size > 0 && ( block.get( 0 )
                    .startsWith( "=" ) || ( size > 1 && block.get( 1 )
                    .startsWith( "=" ) ) );
        }

        @Override
        String process( Block block, Block previousBlock, ExecutionEngine engine )
        {
            String title = block.lines.get( 0 )
                    .replace( "=", "" )
                    .trim();
            String id = "cypherdoc-" + title.toLowerCase()
                    .replace( ' ', '-' );
            return "[[" + id + "]]" + CypherDoc.EOL + "= " + title + " ="
                   + CypherDoc.EOL;
        }
    },
    QUERY
    {
        @Override
        boolean isA( List<String> block )
        {
            String first = block.get( 0 );
            if ( !first.startsWith( "[" ) )
            {
                return false;
            }
            if ( first.contains( "source" ) && first.contains( "cypher" ) )
            {
                return true;
            }
            if ( block.size() > 4 && first.startsWith( "[[" ) )
            {
                String second = block.get( 1 );
                if ( second.contains( "source" ) && second.contains( "cypher" ) )
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        String process( Block block, Block previousBlock, ExecutionEngine engine )
        {
            List<String> queryHeader = new ArrayList<String>();
            List<String> queryLines = new ArrayList<String>();
            List<String> testLines = new ArrayList<String>();
            boolean queryStarted = false;
            boolean queryEnded = false;
            for ( String line : block.lines )
            {
                if ( !queryStarted )
                {
                    if ( line.startsWith( "----" ) )
                    {
                        queryStarted = true;
                    }
                    else
                    {
                        queryHeader.add( line );
                    }
                }
                else if ( queryStarted && !queryEnded )
                {
                    if ( line.startsWith( "----" ) )
                    {
                        queryEnded = true;
                    }
                    else
                    {
                        queryLines.add( line );
                    }
                }
                else if ( queryEnded )
                {
                    testLines.add( line );
                }
            }
            String query = StringUtils.join( queryLines, "\n" );
            String result = engine.execute( query )
                    .dumpToString();
            for ( String test : testLines )
            {
                if ( !result.contains( test ) )
                {
                    throw new IllegalArgumentException(
                            "Query result doesn't contain the string: '" + test
                                    + "'. The query:" + block.toString()
                                    + CypherDoc.EOL + CypherDoc.EOL + result );
                }

            }
            StringBuilder output = new StringBuilder( 512 );
            output.append( StringUtils.join( queryHeader, CypherDoc.EOL ) )
                    .append( CypherDoc.EOL )
                    .append( "----" )
                    .append( CypherDoc.EOL )
                    .append( StringUtils.join( queryLines, CypherDoc.EOL ) )
                    .append( CypherDoc.EOL )
                    .append( "----" )
                    .append( CypherDoc.EOL )
                    .append( CypherDoc.EOL )
                    .append( AsciidocHelper.createQueryResultSnippet( result ) );
            return output.toString();
        }
    },
    TEXT
    {
        @Override
        boolean isA( List<String> block )
        {
            return true;
        }
    };

    abstract boolean isA( List<String> block );

    String process( Block block, Block previousBlock, ExecutionEngine engine )
    {
        return StringUtils.join( block.lines, CypherDoc.EOL );
    }
}
