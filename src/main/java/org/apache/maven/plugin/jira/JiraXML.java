package org.apache.maven.plugin.jira;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML parser for <code>JiraIssue</code>s. This works on an XML file downloaded
 * from JIRA and creates a List of issues that is exposed to the user of the
 * class.
 *
 * @version $Id$
 */
public class JiraXML
    extends DefaultHandler
{
    private List issueList;

    private String currentElement;

    private String currentParent = "";

    private JiraIssue issue;

    public JiraXML( String xmlPath )
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        issueList = new ArrayList();

        try
        {
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse( new File( xmlPath ), this );
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
    }

    public void startElement( String namespaceURI, String sName, String qName, Attributes attrs )
        throws SAXException
    {
        if ( qName.equals( "item" ) )
        {
            issue = new JiraIssue();

            currentParent = "item";
        }
    }

    public void endElement( String namespaceURI, String sName, String qName )
        throws SAXException
    {
        if ( qName.equals( "item" ) )
        {
            issueList.add( issue );

            currentParent = "";
        }
        else if ( qName.equals( "key" ) )
        {
            issue.setKey( currentElement );
        }
        else if ( qName.equals( "summary" ) )
        {
            issue.setSummary( currentElement );
        }
        else if ( qName.equals( "type" ) )
        {
            issue.setType( currentElement );
        }
        else if ( qName.equals( "link" ) && currentParent.equals( "item" ) )
        {
            issue.setLink( currentElement );
        }
        else if ( qName.equals( "priority" ) )
        {
            issue.setPriority( currentElement );
        }
        else if ( qName.equals( "status" ) )
        {
            issue.setStatus( currentElement );
        }
        else if ( qName.equals( "resolution" ) )
        {
            issue.setResolution( currentElement );
        }
        else if ( qName.equals( "assignee" ) )
        {
            issue.setAssignee( currentElement );
        }
        else if ( qName.equals( "reporter" ) )
        {
            issue.setReporter( currentElement );
        }
        else if ( qName.equals( "version" ) )
        {
            issue.setVersion( currentElement );
        }
        else if ( qName.equals( "fixVersion" ) )
        {
            issue.setFixVersion( currentElement );
        }
        else if ( qName.equals( "component" ) )
        {
            issue.setComponent( currentElement );
        }

        currentElement = "";
    }

    public void characters( char[] buf, int offset, int len )
        throws SAXException
    {
        String s = new String( buf, offset, len );

        if ( !s.trim().equals( "" ) )
        {
            currentElement = currentElement + s.trim() + "\n";
        }
    }

    public List getIssueList()
    {
        return this.issueList;
    }
}
