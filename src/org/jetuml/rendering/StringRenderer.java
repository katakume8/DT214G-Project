/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import java.util.Collections;
import java.util.EnumSet;

import org.jetuml.annotations.Immutable;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.geom.Alignment;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A class to render strings with various decorations: underline, bold,
 * with different alignments.
 */
@Immutable
public final class StringRenderer
{
	private static final Text TEXT_NODE = new Text();
	
	/**
	 * Various text decorations.
	 */
	public enum Decoration
	{
		BOLD, ITALIC, UNDERLINED
	}
	
	private final Alignment aAlign;
	private final EnumSet<Decoration> aDecorations;

	/**
	 * Creates a new String Renderer.
	 * 
	 * @param pPosition The desired alignment.
	 * @param pDecorations The desired decorations.
	 */
	public StringRenderer(Alignment pPosition, Decoration... pDecorations)
	{
		aAlign = pPosition;
		aDecorations = EnumSet.noneOf(Decoration.class);
		Collections.addAll(aDecorations, pDecorations);
	}
	
	/**
	 * Draws the string inside a given bounding box. Does not draw a blank string.
	 * Automatically splits lines and draws each separately.
	 * 
	 * @param pString The string to draw.
	 * @param pBoundingBox the rectangle into which to place the string.
	 * @param pContext The rendering context on which to draw the text.
	 */
	public void draw(String pString, Rectangle pBoundingBox, RenderingContext pContext)
	{
		int currentY = pBoundingBox.y();
		for (String line : pString.split("\n"))
		{
			drawSingleLine(line, new Rectangle(pBoundingBox.x(), currentY, pBoundingBox.width(), lineHeight()), pContext);
			currentY += lineHeight();
		}
	}
	
	private void drawSingleLine(String pString, Rectangle pBoundingBox, RenderingContext pContext)
	{
		assert !pString.contains("\n");
		
		if (pString.trim().isBlank())
		{
			return;
		}
		pContext.drawText(pString, pBoundingBox, aAlign, ColorScheme.get().stroke(),
				font(), fontDimension());

		if( aDecorations.contains(Decoration.UNDERLINED))
		{
			final int textWidth = getDimension(pString).width();
			int x1 = pBoundingBox.x();
			if (aAlign == Alignment.CENTER)
			{
				x1 += (pBoundingBox.width() - textWidth)/2;
			}
			/* We position the underline in the middle of the baseline offset */
			int y = pBoundingBox.maxY() - fontDimension().baselineOffset()/2;
			pContext.strokeLine(x1, y, x1 + textWidth, y, ColorScheme.get().stroke(), LineStyle.SOLID);
		}
	}

	/**
	 * Gets the width and height required to show pString, including padding
	 * around the string.
	 * 
	 * @param pString The input string.
	 * @return The dimension pString will use on the screen.
	 * @pre pString != null.
	 */
	public Dimension getDimension(String pString)
	{
		assert pString != null;
		if( pString.length() == 0 )
		{
			return Dimension.NULL;
		}
		TEXT_NODE.setFont(font());
		TEXT_NODE.setText(pString);
		Bounds bounds = TEXT_NODE.getLayoutBounds();
		return new Dimension(GeomUtils.round(bounds.getWidth()), GeomUtils.round(bounds.getHeight()));
	}
	
	/**
	 * @return The height of one line of text, including skip.
	 */
	public int lineHeight()
	{
		return getDimension("|").height();
	}
	
	/**
	 * @return The dimensions of the current font.
	 */
	public FontDimension fontDimension()
	{
		return new FontDimension(lineHeight(), lineHeight()- baselineOffset(font()));
	}
	
	private Font font()
	{
		if( aDecorations.contains(Decoration.BOLD) && aDecorations.contains(Decoration.ITALIC) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontWeight.BOLD,
					FontPosture.ITALIC, UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		else if( aDecorations.contains(Decoration.BOLD) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontWeight.BOLD,
					UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		else if( aDecorations.contains(Decoration.ITALIC) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontPosture.ITALIC,
					UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		return Font.font(UserPreferences.instance().getString(StringPreference.fontName),
				UserPreferences.instance().getInteger(IntegerPreference.fontSize));
	}
	
	/**
	 * Returns the distance between the top and baseline of a single lined text.
	 * 
	 * @param pFont The font used for the metric.
	 * @return the distance above the baseline for a single lined text.
	 * @pre pFont != null
	 */
	private static int baselineOffset(Font pFont)
	{
		assert pFont != null;
		
		TEXT_NODE.setFont(pFont);
		TEXT_NODE.setText("|");
		return GeomUtils.round(TEXT_NODE.getBaselineOffset());
	}
}
