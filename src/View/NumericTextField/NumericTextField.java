package View.NumericTextField;
/**
 Core SWING Advanced Programming
 By Kim Topley
 ISBN: 0 13 083292 8
 Publisher: Prentice Hall
 @see    http://www.java2s.com/Code/Java/Swing-JFC/NumericTextField.htm
 **/

import View.EventListener.InvalidInputListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;

public class NumericTextField extends JTextField implements
        NumericPlainDocument.InsertErrorListener, DocumentListener {
    private Border oldBorder;
    private java.util.List<InvalidInputListener> listeners = new ArrayList<InvalidInputListener>();

    public NumericTextField() {
        this(null, 0, null);
    }

    public NumericTextField(String text, int columns, DecimalFormat format) {
        super(null, text, columns);

        NumericPlainDocument numericDoc = (NumericPlainDocument) getDocument();
        if (format != null) {
            numericDoc.setFormat(format);
        }
        numericDoc.addInsertErrorListener(this);
        numericDoc.addDocumentListener(this);
    }

    public NumericTextField(int columns, DecimalFormat format) {
        this(null, columns, format);
    }

    public NumericTextField(double d) {
        this(d + "");
    }

    public NumericTextField(String text) {
        this(text, 0, null);
    }

    public NumericTextField(String text, int columns) {
        this(text, columns, null);
    }

    public void setFormat(DecimalFormat format) {
        ((NumericPlainDocument) getDocument()).setFormat(format);
    }

    public DecimalFormat getFormat() {
        return ((NumericPlainDocument) getDocument()).getFormat();
    }

    public void formatChanged() {
        // Notify change of format attributes.
        setFormat(getFormat());
    }

    // Methods to get the field value
    public Long getLongValue() throws ParseException {
        return ((NumericPlainDocument) getDocument()).getLongValue();
    }

    public Double getDoubleValue() throws ParseException {
        return ((NumericPlainDocument) getDocument()).getDoubleValue();
    }

    public Number getNumberValue() throws ParseException {
        return ((NumericPlainDocument) getDocument()).getNumberValue();
    }

    // Methods to install numeric values
    public void setValue(Number number) {
        setText(getFormat().format(number));
    }

    public void setValue(long l) {
        setText(getFormat().format(l));
        ;
    }

    public void setValue(double d) {
        setText(getFormat().format(d));
    }

    public void normalize() throws ParseException {
        // format the value according to the format string
        setText(getFormat().format(getNumberValue()));
    }

    // Override to handle insertion error
    public void insertFailed(NumericPlainDocument doc, int offset, String str,
                             AttributeSet a) {
        // By default, just beep
        Toolkit.getDefaultToolkit().beep();
        if (this.oldBorder == null)
            oldBorder = this.getBorder();
        this.setBorder(BorderFactory.createLineBorder(Color.red));
        notifyListener(true);
    }

    // Method to create default model
    protected Document createDefaultModel() {
        return new NumericPlainDocument();
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        removeErrorBorder();
    }

    private void removeErrorBorder() {
        notifyListener(false);
        if (this.oldBorder == null)
            oldBorder = this.getBorder();
        this.setBorder(oldBorder);
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        removeErrorBorder();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        removeErrorBorder();
    }

    public void addInvalidInputListener(InvalidInputListener i) {
        this.listeners.add(i);
    }

    private void notifyListener(boolean isInvalid) {
        for (InvalidInputListener i : listeners)
            i.statusUpdate(isInvalid);
    }
}

class NumericPlainDocument extends PlainDocument {
    public NumericPlainDocument() {
        setFormat(null);
    }

    public NumericPlainDocument(DecimalFormat format) {
        setFormat(format);
    }

    public NumericPlainDocument(AbstractDocument.Content content,
                                DecimalFormat format) {
        super(content);
        setFormat(format);

        try {
            format
                    .parseObject(content.getString(0, content.length()),
                            parsePos);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Initial content not a valid number");
        }

        if (parsePos.getIndex() != content.length() - 1) {
            throw new IllegalArgumentException(
                    "Initial content not a valid number");
        }
    }

    public void setFormat(DecimalFormat fmt) {
        this.format = fmt != null ? fmt : (DecimalFormat) defaultFormat.clone();

        decimalSeparator = format.getDecimalFormatSymbols()
                .getDecimalSeparator();
        groupingSeparator = format.getDecimalFormatSymbols()
                .getGroupingSeparator();
        positivePrefix = format.getPositivePrefix();
        positivePrefixLen = positivePrefix.length();
        negativePrefix = format.getNegativePrefix();
        negativePrefixLen = negativePrefix.length();
        positiveSuffix = format.getPositiveSuffix();
        positiveSuffixLen = positiveSuffix.length();
        negativeSuffix = format.getNegativeSuffix();
        negativeSuffixLen = negativeSuffix.length();
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public Number getNumberValue() throws ParseException {
        try {
            String content = getText(0, getLength());
            parsePos.setIndex(0);
            Number result = format.parse(content, parsePos);
            if (parsePos.getIndex() != getLength()) {
                throw new ParseException("Not a valid number: " + content, 0);
            }

            return result;
        } catch (BadLocationException e) {
            throw new ParseException("Not a valid number", 0);
        }
    }

    public Long getLongValue() throws ParseException {
        Number result = getNumberValue();
        if ((result instanceof Long) == false) {
            throw new ParseException("Not a valid long", 0);
        }

        return (Long) result;
    }

    public Double getDoubleValue() throws ParseException {
        Number result = getNumberValue();
        if ((result instanceof Long) == false
                && (result instanceof Double) == false) {
            throw new ParseException("Not a valid double", 0);
        }

        if (result instanceof Long) {
            result = new Double(result.doubleValue());
        }

        return (Double) result;
    }

    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
        if (str == null || str.length() == 0) {
            return;
        }

        Content content = getContent();
        int length = content.length();
        int originalLength = length;

        parsePos.setIndex(0);

        // Create the result of inserting the new data,
        // but ignore the trailing newline
        String targetString = content.getString(0, offset) + str
                + content.getString(offset, length - offset - 1);

        // Parse the input string and check for errors
        do {
            boolean gotPositive = targetString.startsWith(positivePrefix);
            boolean gotNegative = targetString.startsWith(negativePrefix);

            length = targetString.length();

            // If we have a valid prefix, the parse fails if the
            // suffix is not present and the error is reported
            // at index 0. So, we need to add the appropriate
            // suffix if it is not present at this point.
            if (gotPositive == true || gotNegative == true) {
                String suffix;
                int suffixLength;
                int prefixLength;

                if (gotPositive == true && gotNegative == true) {
                    // This happens if one is the leading part of
                    // the other - e.g. if one is "(" and the other "(("
                    if (positivePrefixLen > negativePrefixLen) {
                        gotNegative = false;
                    } else {
                        gotPositive = false;
                    }
                }

                if (gotPositive == true) {
                    suffix = positiveSuffix;
                    suffixLength = positiveSuffixLen;
                    prefixLength = positivePrefixLen;
                } else {
                    // Must have the negative prefix
                    suffix = negativeSuffix;
                    suffixLength = negativeSuffixLen;
                    prefixLength = negativePrefixLen;
                }

                // If the string consists of the prefix alone,
                // do nothing, or the result won't parse.
                if (length == prefixLength) {
                    break;
                }

                // We can't just add the suffix, because part of it
                // may already be there. For example, suppose the
                // negative prefix is "(" and the negative suffix is
                // "$)". If the user has typed "(345$", then it is not
                // correct to add "$)". Instead, only the missing part
                // should be added, in this case ")".
                if (targetString.endsWith(suffix) == false) {
                    int i;
                    for (i = suffixLength - 1; i > 0; i--) {
                        if (targetString
                                .regionMatches(length - i, suffix, 0, i)) {
                            targetString += suffix.substring(i);
                            break;
                        }
                    }

                    if (i == 0) {
                        // None of the suffix was present
                        targetString += suffix;
                    }

                    length = targetString.length();
                }
            }

            format.parse(targetString, parsePos);

            int endIndex = parsePos.getIndex();
            if (endIndex == length) {
                break; // Number is acceptable
            }

            // Parse ended early
            // Since incomplete numbers don't always parse, try
            // to work out what went wrong.
            // First check for an incomplete positive prefix
            if (positivePrefixLen > 0 && endIndex < positivePrefixLen
                    && length <= positivePrefixLen
                    && targetString.regionMatches(0, positivePrefix, 0, length)) {
                break; // Accept for now
            }

            // Next check for an incomplete negative prefix
            if (negativePrefixLen > 0 && endIndex < negativePrefixLen
                    && length <= negativePrefixLen
                    && targetString.regionMatches(0, negativePrefix, 0, length)) {
                break; // Accept for now
            }

            // Allow a number that ends with the group
            // or decimal separator, if these are in use
            char lastChar = targetString.charAt(originalLength - 1);
            int decimalIndex = targetString.indexOf(decimalSeparator);
            if (format.isGroupingUsed() && lastChar == groupingSeparator
                    && decimalIndex == -1) {
                // Allow a "," but only in integer part
                break;
            }

            if (format.isParseIntegerOnly() == false
                    && lastChar == decimalSeparator
                    && decimalIndex == originalLength - 1) {
                // Allow a ".", but only one
                break;
            }

            // No more corrections to make: must be an error
            if (errorListener != null) {
                errorListener.insertFailed(this, offset, str, a);
            }
            return;
        } while (true == false);

        // Finally, add to the model
        super.insertString(offset, str, a);
    }

    public void addInsertErrorListener(InsertErrorListener l) {
        if (errorListener == null) {
            errorListener = l;
            return;
        }
        throw new IllegalArgumentException(
                "InsertErrorListener already registered");
    }

    public void removeInsertErrorListener(InsertErrorListener l) {
        if (errorListener == l) {
            errorListener = null;
        }
    }

    public interface InsertErrorListener {
        public abstract void insertFailed(NumericPlainDocument doc, int offset,
                                          String str, AttributeSet a);
    }

    protected InsertErrorListener errorListener;

    protected DecimalFormat format;

    protected char decimalSeparator;

    protected char groupingSeparator;

    protected String positivePrefix;

    protected String negativePrefix;

    protected int positivePrefixLen;

    protected int negativePrefixLen;

    protected String positiveSuffix;

    protected String negativeSuffix;

    protected int positiveSuffixLen;

    protected int negativeSuffixLen;

    protected ParsePosition parsePos = new ParsePosition(0);

    protected static DecimalFormat defaultFormat = new DecimalFormat();
}