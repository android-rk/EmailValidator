package rk.emailvalidator.emailvalidator4j.parser;

import java.util.List;

import rk.emailvalidator.emailvalidator4j.lexer.EmailLexer;
import rk.emailvalidator.emailvalidator4j.lexer.Tokens;
import rk.emailvalidator.emailvalidator4j.parser.exception.InvalidCharacters;
import rk.emailvalidator.emailvalidator4j.parser.exception.InvalidEmail;
import rk.emailvalidator.emailvalidator4j.parser.exception.NoLocalPart;

public class Email {
    private final EmailLexer lexer;
    private final LocalPart localPartParser;
    private final DomainPart domainPartParser;

    public Email(EmailLexer lexer) {
        this.lexer = lexer;
        this.localPartParser = new LocalPart(lexer);
        this.domainPartParser = new DomainPart(lexer);
    }

    public void parse(String email) throws InvalidEmail {
        this.lexer.lex(email);

        if (!this.lexer.find(Tokens.AT)) {
            throw new NoLocalPart("No local part found");
        }

        if (this.lexer.hasInvalidTokens()) {
            throw new InvalidCharacters("Found invalid or malformed characters");
        }

        this.localPartParser.parse(email);
        this.domainPartParser.parse(this.lexer.toString());
    }

    public List getWarnings() {
        List<Warnings> warnings = this.localPartParser.getWarnings();
        warnings.addAll(this.domainPartParser.getWarnings());
        return warnings;
    }

    public String getDomainPart() {
        return this.domainPartParser.getParsed().replace("@", "");
    }
}
