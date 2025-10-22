/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import javafx.scene.text.Font;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Alignment;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.StringRenderer.Decoration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.ArgumentCaptor;
import java.util.List;

public class TestStringViewer 
{

	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	
	private StringRenderer topCenter;
	private StringRenderer topCenterBold;
	private RenderingContext renderingContextMock;


	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, UserPreferences.DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}
	
	@BeforeEach
	public void setup()
	{
		topCenter = new StringRenderer(Alignment.CENTER);
		topCenterBold = new StringRenderer(Alignment.CENTER, Decoration.BOLD);
		renderingContextMock = mock(RenderingContext.class);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimensionDefaultFont()
	{
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(73, 16), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(79, 16), topCenterBold.getDimension("Display String"));
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimension8ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 8);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(49, 11), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(53, 11), topCenterBold.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimension24ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 24);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(146, 32), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(158, 32), topCenterBold.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}

	@Test
	@DisplayName("Should kill draw name mutation:Removed call and replaced operator")
	void killDrawMethodMutation(){
		StringRenderer renderer = spy(new StringRenderer(Alignment.CENTER));
		Rectangle box = new Rectangle(10, 100, 200, 20);
		String input = "line1\nline2\nline3";

		renderer.draw(input, box, renderingContextMock);

		ArgumentCaptor<Rectangle> rectCaptor = ArgumentCaptor.forClass(Rectangle.class);
		verify(renderingContextMock, times(3)).drawText(
				anyString(),
				rectCaptor.capture(),
				any(),
				any(),
				any(),
				any()
		);

		List<Rectangle> rects = rectCaptor.getAllValues();
		int lineHeight = renderer.lineHeight();
		int startY = box.y();

		assertEquals(startY, rects.get(0).y());
		assertEquals(startY + lineHeight, rects.get(1).y());
		assertEquals(startY + 2 * lineHeight, rects.get(2).y());
	}

	@Test
	@DisplayName("Should kill drawSingleLine method mutation: replace equality check with false")
	void shouldKillDrawSingleLineMethodMutationReplaceEqualityCheck(){
		StringRenderer renderer = spy(new StringRenderer(Alignment.CENTER));
		Rectangle box = new Rectangle(10, 100, 200, 20);
		String input = "First\n   \nSecond";

		renderer.draw(input, box, renderingContextMock);

		verify(renderingContextMock, times(2)).drawText(
				anyString(),
				any(Rectangle.class),
				any(),
				any(),
				any(),
				any()
		);
	}

	@Test
	@DisplayName("Should not underline when UNDERLINED decoration is absent")
	void shouldNotUnderlineWhenDecorationAbsent() {
		StringRenderer renderer = new StringRenderer(Alignment.CENTER);
		Rectangle box = new Rectangle(10, 100, 200, 20);
		String input = "First\n_aaa_\nSecond";

		renderer.draw(input, box, renderingContextMock);

		verify(renderingContextMock, never()).strokeLine(
				anyInt(),
				anyInt(),
				anyInt(),
				anyInt(),
				any(),
				any()
		);
	}

	@Test
	@DisplayName("Should underline when UNDERLINED decoration is used")
	void drawShouldUnderlineWhenDecorationIsPresent() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.LEFT, StringRenderer.Decoration.UNDERLINED);
		Rectangle box = new Rectangle(100, 100, 100, 20);
		String text = "U";

		renderer.draw(text, box, context);

		verify(context, atLeastOnce()).strokeLine(
				anyInt(),
				anyInt(),
				anyInt(),
				anyInt(),
				any(),
				eq(LineStyle.SOLID)
		);
	}

	@Test
	@DisplayName("Should underline when UNDERLINED decoration is used")
	void shouldCenterUnderlineWhenAlignmentIsCenter() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.CENTER, StringRenderer.Decoration.UNDERLINED);
		Rectangle box = new Rectangle(100, 100, 100, 20);
		String text = "U";

		renderer.draw(text, box, context);

		ArgumentCaptor<Integer> x1Captor = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Integer> x2Captor = ArgumentCaptor.forClass(Integer.class);

		verify(context, atLeastOnce()).strokeLine(
				x1Captor.capture(),
				anyInt(),
				x2Captor.capture(),
				anyInt(),
				any(),
				any()
		);
		int underlineStart = x1Captor.getValue();
		int underlineEnd = x2Captor.getValue();
		int textWidth = renderer.getDimension(text).width();
		int expectedStart = box.x() + (box.width() - textWidth) / 2;

		assertEquals(expectedStart, underlineStart,
				"Underline start should be centered inside the box");

		assertEquals(textWidth, underlineEnd - underlineStart,
				"Underline width should match text width");
	}

	@Test
	@DisplayName("Should kill drawSingleLineMutation: replaced operation")
	void shouldKillDrawSingleLineMethodMutationReplaceOperation() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.CENTER, StringRenderer.Decoration.UNDERLINED);
		Rectangle box = new Rectangle(100, 100, 100, 20);
		String text = "Uuuuuuuu";

		renderer.draw(text, box, context);

		ArgumentCaptor<Integer> yCaptor = ArgumentCaptor.forClass(Integer.class);

		verify(context, atLeastOnce()).strokeLine(
				anyInt(),
				yCaptor.capture(),
				anyInt(),
				anyInt(),
				any(),
				any()
		);

		int actualY = yCaptor.getValue();
		int currentY = box.y();
		int lineHeight = renderer.lineHeight();
		Rectangle lineBox = new Rectangle(box.x(), currentY, box.width(), lineHeight);

		int expectedY = lineBox.maxY() - renderer.fontDimension().baselineOffset()/2;

		assertEquals(expectedY, actualY, "Underline Y must match renderer's calculation");
	}

	@Test
	@DisplayName("Should return the line height")
	void shouldReturnLineHeight() {
		StringRenderer renderer = new StringRenderer(Alignment.CENTER);
		Dimension dim = renderer.getDimension("|");
		int expectedLineHeight = renderer.lineHeight();

		assertEquals(dim.height(), expectedLineHeight, "lineHeight must match the actual text height");
	}


	@Test
	@DisplayName("Should return the dimension of the current font")
	void shouldReturnFontDimension() {
		StringRenderer renderer = new StringRenderer(Alignment.CENTER);
		FontDimension fontDim = renderer.fontDimension();

		assertTrue(fontDim.baselineOffset() <= fontDim.lineHeight(),
				"FontDimension baselineOffset must be less than or equal to lineHeight");
	}

	@Test
	@DisplayName("Should set the font to BOLD and ITALIC")
	void shouldAssignBoldAndItalicDecoration() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.LEFT, Decoration.BOLD,Decoration.ITALIC);
		renderer.draw("Hello", new Rectangle(0,0,100,20), context);

		ArgumentCaptor<Font> fontCaptor = ArgumentCaptor.forClass(Font.class);
		verify(context).drawText(anyString(), any(), any(), any(), fontCaptor.capture(), any());
		Font drawnFont = fontCaptor.getValue();

		assertTrue(drawnFont.getStyle().contains("Bold"), "Font should be bold");
		assertTrue(drawnFont.getStyle().contains("Italic"), "Font should be italic");
	}

	@Test
	@DisplayName("Should set the font to ITALIC")
	void shouldAssignItalicDecoration() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.LEFT,Decoration.ITALIC);
		renderer.draw("Hello", new Rectangle(0,0,100,20), context);

		ArgumentCaptor<Font> fontCaptor = ArgumentCaptor.forClass(Font.class);
		verify(context).drawText(anyString(), any(), any(), any(), fontCaptor.capture(), any());
		Font drawnFont = fontCaptor.getValue();

		assertTrue(drawnFont.getStyle().contains("Italic"), "Font should be italic");
	}

	@Test
	@DisplayName("Should set the font to BOLD")
	void shouldAssignBoldDecoration() {
		RenderingContext context = spy(RenderingContext.class);
		StringRenderer renderer = new StringRenderer(Alignment.LEFT, Decoration.BOLD);

		renderer.draw("Hello", new Rectangle(0,0,100,20), context);
		ArgumentCaptor<Font> fontCaptor = ArgumentCaptor.forClass(Font.class);
		verify(context).drawText(anyString(), any(), any(), any(), fontCaptor.capture(), any());
		Font drawnFont = fontCaptor.getValue();

		assertTrue(drawnFont.getStyle().contains("Bold"), "Font should be bold");
	}

}
