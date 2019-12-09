import java.util.Objects;

public class Token {
    private String str;
    private int length;
    private String docId;
    private boolean inTitle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(str, token.str) &&
                Objects.equals(docId, token.docId);
    }

    public void setStr(String str) {
        this.str = str;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setInTitle(boolean inTitle) {
        this.inTitle = inTitle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(str);
    }

    public Token(String str, String docId, boolean inTitle) {
        this.str = str;
        this.length = str.length();
        this.docId = docId;
        this.inTitle = inTitle;
    }

    public String getDocId() {
        return docId;
    }

    public String getStr() {
        return str;
    }

    public boolean isInTitle() {
        return inTitle;
    }

    public int getLength() {
        return length;
    }
}
