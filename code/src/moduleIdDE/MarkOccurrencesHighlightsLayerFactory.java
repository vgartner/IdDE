package moduleIdDE;

import javax.swing.text.Document;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;

public class MarkOccurrencesHighlightsLayerFactory implements HighlightsLayerFactory {

    public static MarkOccurrencesHighlighter getMarkOccurrencesHighlighter(Document doc) {
        MarkOccurrencesHighlighter highlighter = (MarkOccurrencesHighlighter) doc.getProperty(MarkOccurrencesHighlighter.class);
        if (highlighter == null) {
            doc.putProperty(MarkOccurrencesHighlighter.class, highlighter = new MarkOccurrencesHighlighter(doc));
        }
        return highlighter;
    }

    public HighlightsLayer[] createLayers(Context context) {
        return new HighlightsLayer[]{
                    HighlightsLayer.create(
                    MarkOccurrencesHighlighter.class.getName(),
                    ZOrder.CARET_RACK.forPosition(2000),
                    true,
                    getMarkOccurrencesHighlighter(context.getDocument()).getHighlightsBag())
                };
    }
}