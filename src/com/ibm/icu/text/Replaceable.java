/*
 *******************************************************************************
 * Copyright (C) 1996-2000, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 *
 * $Source: /xsrl/Nsvn/icu/icu4j/src/com/ibm/icu/text/Replaceable.java,v $ 
 * $Date: 2002/07/02 23:50:34 $ 
 * $Revision: 1.7 $
 *
 *****************************************************************************************
 */
package com.ibm.icu.text;

/**
 * <code>Replaceable</code> is an interface that supports the
 * operation of replacing a substring with another piece of text.
 * <code>Replaceable</code> is needed in order to change a piece of
 * text while retaining metadata.  Metadata is data other than the
 * Unicode characters returned by char32At().  One example of metadata
 * is style tags; another is an edit history, marking each character
 * with an author and revision number.  For example, if the string
 * "the <b>bold</b> font" has range (4, 8) replaced with "strong",
 * then it becomes "the <b>strong</b> font".
 *
 * <p>If a subclass supports metadata, then typically the behavior of
 * <code>replace()</code> is the following:
 * <ul>
 *   <li>Set the metadata of the new text to the metadata of the first
 *   character replaced</li>
 *   <li>If no characters are replaced, use the metadata of the
 *   previous character</li>
 *   <li>If there is no previous character (i.e. start == 0), use the
 *   following character</li>
 *   <li>If there is no following character (i.e. the replaceable was
 *   empty), use default metadata.<br>
 *   </li>
 * </ul>
 * If this is not the behavior, the subclass should document any differences.
 * 
 * <p>Copyright &copy; IBM Corporation 1999.  All rights reserved.
 *
 * @author Alan Liu
 * @version $RCSfile: Replaceable.java,v $ $Revision: 1.7 $ $Date: 2002/07/02 23:50:34 $
 */
public interface Replaceable {
    /**
     * Returns the number of 16-bit code units in the text.
     * @return number of 16-bit code units in text
     */ 
    int length();

    /**
     * Returns the 16-bit code unit at the given offset into the text.
     * @param offset an integer between 0 and <code>length()</code>-1
     * inclusive
     * @return 16-bit code unit of text at given offset
     */
    char charAt(int offset);

    /**
     * Returns the 32-bit code point at the given 16-bit offset into
     * the text.  This assumes the text is stored as 16-bit code units
     * with surrogate pairs intermixed.  If the offset of a leading or
     * trailing code unit of a surrogate pair is given, return the
     * code point of the surrogate pair.
     *
     * <p>Most subclasses can return
     * <code>com.ibm.icu.text.UTF16.charAt(this, offset)</code>.
     * @param offset an integer between 0 and <code>length()</code>-1
     * inclusive
     * @return 32-bit code point of text at given offset
     */
    int char32At(int offset);

    /**
     * Copies characters from this object into the destination
     * character array.  The first character to be copied is at index
     * <code>srcStart</code>; the last character to be copied is at
     * index <code>srcLimit-1</code> (thus the total number of
     * characters to be copied is <code>srcLimit-srcStart</code>). The
     * characters are copied into the subarray of <code>dst</code>
     * starting at index <code>dstStart</code> and ending at index
     * <code>dstStart + (srcLimit-srcStart) - 1</code>.
     *
     * @param srcStart the beginning index to copy, inclusive; <code>0
     * <= start <= limit</code>.
     * @param srcLimit the ending index to copy, exclusive;
     * <code>start <= limit <= length()</code>.
     * @param dst the destination array.
     * @param dstStart the start offset in the destination array.
     */
    void getChars(int srcStart, int srcLimit, char dst[], int dstStart);

    /**
     * Replaces a substring of this object with the given text.
     *
     * <p>Subclasses must ensure that if the text between start and
     * limit is equal to the replacement text, that replace has no
     * effect. That is, any metadata
     * should be unaffected. In addition, subclasses are encouraged to
     * check for initial and trailing identical characters, and make a
     * smaller replacement if possible. This will preserve as much
     * metadata as possible.
     * @param start the beginning index, inclusive; <code>0 <= start
     * <= limit</code>.
     * @param limit the ending index, exclusive; <code>start <= limit
     * <= length()</code>.
     * @param text the text to replace characters <code>start</code>
     * to <code>limit - 1</code>
     */
    void replace(int start, int limit, String text);

    /**
     * Replaces a substring of this object with the given text.
     *
     * <p>Subclasses must ensure that if the text between start and
     * limit is equal to the replacement text, that replace has no
     * effect. That is, any metadata
     * should be unaffected. In addition, subclasses are encouraged to
     * check for initial and trailing identical characters, and make a
     * smaller replacement if possible. This will preserve as much
     * metadata as possible.
     * @param start the beginning index, inclusive; <code>0 <= start
     * <= limit</code>.
     * @param limit the ending index, exclusive; <code>start <= limit
     * <= length()</code>.
     * @param chars the text to replace characters <code>start</code>
     * to <code>limit - 1</code>
     * @param charsStart the beginning index into <code>chars</code>,
     * inclusive; <code>0 <= start <= limit</code>.
     * @param charsLen the number of characters of <code>chars</code>.
     */
    void replace(int start, int limit, char[] chars,
                 int charsStart, int charsLen);
    // Note: We use length rather than limit to conform to StringBuffer
    // and System.arraycopy.

    /**
     * Copies a substring of this object, retaining metadata
     * This method is used to duplicate or reorder substrings.
     * The destination index must not overlap the source range.
     * If <code>hasMetaData()</code> returns false, subclasses
     * may use the naive implementation:
     *
     * <pre> char[] text = new char[limit - start];
     * getChars(start, limit, text, 0);
     * replace(dest, dest, text, 0, limit - start);</pre>
     * 
     * @param start the beginning index, inclusive; <code>0 <= start <=
     * limit</code>.
     * @param limit the ending index, exclusive; <code>start <= limit <=
     * length()</code>.
     * @param dest the destination index.  The characters from
     * <code>start..limit-1</code> will be copied to <code>dest</code>.
     * Implementations of this method may assume that <code>dest <= start ||
     * dest >= limit</code>.
     */
    void copy(int start, int limit, int dest);
    
    /**
     * Returns true if this object contains metadata.  If a
     * Replaceable object has metadata, calls to the Replaceable API
     * must be made so as to preserve metadata.  If it does not, calls
     * to the Replaceable API may be optimized to improve performance.
     * @return true if this object contains metadata
     */
    boolean hasMetaData();
}
