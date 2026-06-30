package com.complexcalc.parser;

import static com.complexcalc.parser.registry.Functions.*;

import com.complexcalc.parser.registry.Token;
import java.util.ArrayList;
import java.util.List;

public class LatexLexer {

    public static List<valueToken> tokenize(String s) {
        List<valueToken> tokens = new ArrayList<>();

        int digitStart = -1;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') continue;

            if (Character.isDigit(c) || c == '.') {
                if (digitStart == -1) digitStart = i;
                continue;
            }

            if (digitStart != -1) {
                tokens.add(new valueToken(Token.NUM, Double.parseDouble(s.substring(digitStart, i))));
                digitStart = -1;
            }
            tokenMatching: switch (c) {
                case '+' -> tokens.add(new valueToken(Token.ADD, 1));
                case '-', '−' -> {
                    Token lastToken;
                    if (i == 0) tokens.add(new valueToken(Token.UMINUS, 3));
                    else {
                        lastToken = tokens.getLast().type();
                        if (
                            lastToken != Token.NUM &&
                            lastToken != Token.VAR &&
                            lastToken != Token.RPAR &&
                            lastToken != Token.RBRACE &&
                            lastToken != Token.FACT
                        ) tokens.add(new valueToken(Token.UMINUS, 3));
                        else tokens.add(new valueToken(Token.MINUS, 1));
                    }
                }
                case '*', '×' -> tokens.add(new valueToken(Token.MULT, 2));
                case '/', '÷' -> tokens.add(new valueToken(Token.DIV, 2));
                case '!' -> tokens.add(new valueToken(Token.FACT, 4));
                case '^' -> tokens.add(new valueToken(Token.POW, 4));
                case '(' -> tokens.add(new valueToken(Token.LPAR, 5));
                case ')' -> tokens.add(new valueToken(Token.RPAR, 5));
                case '{', '[' -> tokens.add(new valueToken(Token.LBRACE, 5));
                case '}', ']' -> tokens.add(new valueToken(Token.RBRACE, 5));
                case '_' -> tokens.add(new valueToken(Token.SUBS, 5));
                case '=' -> tokens.add(new valueToken(Token.EQUALS, 6));
                case '|' -> {
                    if (tokens.getLast().type() == Token.LEFT) {
                        tokens.removeLast();
                        tokens.add(new valueToken(Token.ABS, 4));
                        tokens.add(new valueToken(Token.LPAR, 5));
                    } else if (tokens.getLast().type() == Token.RIGHT) {
                        tokens.removeLast();
                        tokens.add(new valueToken(Token.RPAR, 5));
                    } else throw new IllegalArgumentException("missing side specifier for abs");
                }
                case '\\' -> {
                    for (String operation : wordOperations.keySet()) {
                        if (s.substring(i + 1).startsWith(operation)) {
                            tokens.add(new valueToken(wordOperations.get(operation), 3));
                            i += operation.length();
                            break tokenMatching;
                        }
                    }
                    for (String operation : multipleArgOperations.keySet()) {
                        if (s.substring(i + 1).startsWith(operation)) {
                            tokens.add(new valueToken(multipleArgOperations.get(operation), 3));
                            i += operation.length();
                            break tokenMatching;
                        }
                    }
                    for (String number : numberSymbols.keySet()) {
                        if (s.substring(i + 1).startsWith(number)) {
                            tokens.add(new valueToken(Token.NUM, numberSymbols.get(number)));
                            i += number.length();
                            break tokenMatching;
                        }
                    }
                    if (s.substring(i + 1).startsWith("left")) {
                        tokens.add(new valueToken(Token.LEFT, 0));
                        i += 4;
                    } else if (s.substring(i + 1).startsWith("right")) {
                        tokens.add(new valueToken(Token.RIGHT, 0));
                        i += 5;
                    }
                }
                default -> {
                    if (Character.isAlphabetic(c)) {
                        tokens.add(new valueToken(Token.VAR, c));
                    } else throw new IllegalArgumentException();
                }
            }
        }
        if (digitStart != -1) tokens.add(new valueToken(Token.NUM, Double.parseDouble(s.substring(digitStart))));

        //IS: list of tokens that are allowed between two expressions
        //INFO: implied multiplication doesn't add mult tokens after these
        List<Token> allowedToEnd = new ArrayList<>(
            List.of(Token.ADD, Token.DIV, Token.POW, Token.MULT, Token.MINUS, Token.EQUALS, Token.UMINUS)
        );

        //DOES: insert mult tokens for implied multiplication
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (!allowedToEnd.contains(tokens.get(i).type()) && !allowedToEnd.contains(tokens.get(i + 1).type())) {
                if (
                    wordOperations.containsValue(tokens.get(i).type()) ||
                    multipleArgOperations.containsValue(tokens.get(i).type()) ||
                    Token.LPAR == tokens.get(i).type() ||
                    Token.RPAR == tokens.get(i + 1).type() ||
                    Token.LBRACE == tokens.get(i).type() ||
                    Token.LBRACE == tokens.get(i + 1).type() ||
                    Token.RBRACE == tokens.get(i + 1).type() ||
                    (Token.RBRACE == tokens.get(i).type() && Token.LBRACE == tokens.get(i + 1).type()) ||
                    Token.FACT == tokens.get(i + 1).type()
                ) continue;
                tokens.add(i + 1, new valueToken(Token.MULT, 2));
                i++;
            }
        }
        return tokens;
    }
}
