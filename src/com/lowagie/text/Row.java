/*
 * $Id$
 * $Name$
 *
 * Copyright 1999, 2000, 2001, 2002 by Bruno Lowagie.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU LIBRARY GENERAL PUBLIC LICENSE for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */

package com.lowagie.text;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * A <CODE>Row</CODE> is part of a <CODE>Table</CODE>
 * and contains <bold>nothing but</bold> <CODE>Cells</CODE>.
 * <P>
 * All <CODE>Row</CODE>s are constructed by a <CODE>Table</CODE>-object.
 * You don't have to construct any <CODE>Row</CODE> yourself.
 * In fact you can't construct a <CODE>Row</CODE> outside the package.
 * <P>
 * Since a <CODE>Cell</CODE> can span several rows and/or columns
 * a row contains reserved space without any content. This class
 * manages the reservation on a per row basis.
 *
 * @see   Element
 * @see   Cell
 * @see   Table
 */

public class Row implements Element, MarkupAttributes {

    // membervariables

    /** This is the number of columns in the <CODE>Row</CODE>. */
    protected int columns;

    /** This is a valid position the <CODE>Row</CODE>. */
    // protected int currentColumn;

    /** This is the array that keeps track of reserved cells. */
    protected boolean[] reserved;

    /** This is the array of <CODE>Cell</CODE>s. */
    protected Cell[] cells;

    /** This is the vertical alignment. */
    protected int horizontalAlignment;

    /** This is the vertical alignment. */
    protected int verticalAlignment;

    /** Contains extra markupAttributes */
    protected Properties markupAttributes;

    // constructors

    /**
     * Constructs a <CODE>Row</CODE> with a certain number of <VAR>columns</VAR>.
     *
     * @param columns   a number of columns
     */

    protected Row(int columns) {
        this.columns = columns;
        reserved = new boolean[columns];
        cells = new Cell[columns];
        //      currentColumn = 0;
    }

    // implementation of the Element-methods

    /**
     * Processes the element by adding it (or the different parts) to a
     * <CODE>ElementListener</CODE>.
     *
     * @param listener  an <CODE>ElementListener</CODE>
     * @return  <CODE>true</CODE> if the element was processed successfully
     */
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch(DocumentException de) {
            return false;
        }
    }

    /**
     * Gets the type of the text element.
     *
     * @return  a type
     */

    public int type() {
        return Element.ROW;
    }

    /**
     * Gets all the chunks in this element, which is always empty!
     *
     * @return  an <CODE>ArrayList</CODE>
     */

    public ArrayList getChunks() {
        return new ArrayList();
    }

    /**
     * Returns a <CODE>Row</CODE> that is a copy of this <CODE>Row</CODE>
     * in which a certain column has been deleted.
     *
     * @param column  the number of the column to delete
     */

    void deleteColumn(int column) {
        if ((column >= columns) || (column < 0)) {
            throw new IndexOutOfBoundsException("deleteColumn at illegal index : " + column);
        }
        columns--;
        boolean newReserved[] = new boolean[columns];
        Cell newCells[] = new Cell[columns];

        for (int i = 0; i < column; i++) {
            newReserved[i] = reserved[i];
            newCells[i] = cells[i];
            if (newCells[i] != null && (i + ((Cell) newCells[i]).colspan() > column)) {
                ((Cell) newCells[i]).setColspan(((Cell) cells[i]).colspan() - 1);
            }
        }
        for (int i = column; i < columns; i++) {
            newReserved[i] = reserved[i + 1];
            newCells[i] = cells[i + 1];
        }
        if (cells[column] != null && ((Cell) cells[column]).colspan() > 1) {
            newCells[column] = cells[column];
            ((Cell) newCells[column]).setColspan(((Cell) newCells[column]).colspan() - 1);
        }
        reserved = newReserved;
        cells = newCells;
    }

    // methods


    /**
     * Adds an element to the <CODE>Row</CODE> at the given position.
     *
     * @param       element the element (<CODE>Cell</CODE> or <CODE>Table</CODE>) to add.
     * @param       column  the column position where to add the element.
     */

    void setCell(Cell cell, int column) {
        if (cell == null)
            throw new NullPointerException("addCell - null argument");
        if ((column < 0) || (column > columns))
            throw new IndexOutOfBoundsException("addCell - illegal column argument");

        cells[column] = cell;
        // currentColumn = column + lColspan - 1;

        // System.out.println(" Row.setCell: empty:"+cell.isEmpty()+" colspan: "+cell.colspan()+" @"+column);
    }


    /**
     * Reserves a <CODE>Cell</CODE> in the <CODE>Row</CODE>.
     *
     * @param   column  the column that has to be reserved.
     * @return  <CODE>true</CODE> if the column was reserved, <CODE>false</CODE> if not.
     */

    boolean reserve(int column) {
        return reserve(column, 1);
    }


    /**
     * Reserves a <CODE>Cell</CODE> in the <CODE>Row</CODE>.
     *
     * @param   column  the column that has to be reserved.
     * @param   size    the number of columns
     * @return  <CODE>true</CODE> if the column was reserved, <CODE>false</CODE> if not.
     */

    boolean reserve(int column, int size) {
        if ((column < 0) || ((column + size) > columns))
            throw new IndexOutOfBoundsException("reserve - incorrect column/size");

        for(int i=column; i < column + size; i++) {
            if (true == reserved[i]) {
                // undo reserve
                for(int j=i; j >= column; j--) {
                    reserved[i] = false;
                }
                return false;
            }
            reserved[i] = true;
        }
        return true;
    }

    /**
     * Sets the horizontal alignment.
     *
     * @param value the new value
     */

    public void setHorizontalAlignment(int value) {
        horizontalAlignment = value;
    }

    /**
     * Sets the vertical alignment.
     *
     * @param value the new value
     */

    public void setVerticalAlignment(int value) {
        verticalAlignment = value;
    }

    // methods to retrieve information

    /**
     * Returns true/false when this position in the <CODE>Row</CODE>
     * has been reserved, either filled or through a colspan of an
     * Element.
     *
     * @param       column  the column.
     * @return      <CODE>true</CODE> if the column was reserved, <CODE>false</CODE> if not.
     */

    boolean isReserved(int column) {
        return reserved[column];
    }

    /**
     * Gets a <CODE>Cell</CODE> or <CODE>Table</CODE> from a certain column.
     *
     * @param   column  the column the <CODE>Cell/Table</CODE> is in.
     * @return  the <CODE>Cell</CODE>,<CODE>Table</CODE> or <VAR>Object</VAR> if the column was
     *                  reserved or null if empty.
     */

    public Cell getCell(int column) {
        if ((column < 0) || (column > columns)) {
            throw new IndexOutOfBoundsException("getCell at illegal index :"
                                                + column + " max is " + columns);
        }
        return cells[column];
    }

    /**
     * Checks if the row is empty.
     *
     * @return  <CODE>true</CODE> if none of the columns is reserved.
     * <ea> todo: either the comment or the code is wrong!
     */

    public boolean isEmpty() {
        for (int i = 0; i < columns; i++) {
            if (cells[i] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the index of the current, valid position
     *
     * @return  a value
     */

    /* int validPosition() {
        return currentColumn;
        }*/

    /**
     * Gets the number of columns.
     *
     * @return  a value
     */

    public int columns() {
        return columns;
    }

    /**
     * Gets the horizontal alignment.
     *
     * @return  a value
     */

    public int horizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Gets the vertical alignment.
     *
     * @return  a value
     */

    public int verticalAlignment() {
        return verticalAlignment;
    }

    /**
     * Checks if a given tag corresponds with this object.
     *
     * @param   tag     the given tag
     * @return  true if the tag corresponds
     */

    public static boolean isTag(String tag) {
        return ElementTags.ROW.equals(tag);
    }


    /**
     * @see com.lowagie.text.MarkupAttributes#setMarkupAttribute(java.lang.String, java.lang.String)
     */
    public void setMarkupAttribute(String name, String value) {
        markupAttributes = (markupAttributes == null) ? new Properties() : markupAttributes;
        markupAttributes.put(name, value);
    }

    /**
     * @see com.lowagie.text.MarkupAttributes#setMarkupAttributes(java.util.Properties)
     */
    public void setMarkupAttributes(Properties markupAttributes) {
        this.markupAttributes = markupAttributes;
    }

    /**
     * @see com.lowagie.text.MarkupAttributes#getMarkupAttribute(java.lang.String)
     */
    public String getMarkupAttribute(String name) {
        return (markupAttributes == null) ? null : String.valueOf(markupAttributes.get(name));
    }

    /**
     * @see com.lowagie.text.MarkupAttributes#getMarkupAttributeNames()
     */
    public Set getMarkupAttributeNames() {
        return Chunk.getKeySet(markupAttributes);
    }

    /**
     * @see com.lowagie.text.MarkupAttributes#getMarkupAttributes()
     */
    public Properties getMarkupAttributes() {
        return markupAttributes;
    }

    void printReserved() {
        String lStatus = new String();
        for (int i=0; i < reserved.length; i++) {
            lStatus += isReserved(i) + "  ";
        }
        System.out.println(lStatus);
    }
}
