/* This library is under the 3-Clause BSD License

Copyright (c) 2018-2021, Orange S.A.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.

  3. Neither the name of the copyright holder nor the names of its contributors
     may be used to endorse or promote products derived from this software without
     specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 @author Johannes Heinecke
 @version 2.18.2 as of 31th October 2022
 */
package com.orange.labs.conllparser;

import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.*;

public class CheckCondition {

    ParseTree tree;

    public CheckCondition(String condition, boolean debug) throws ConllException {
        try {
            tree = parse_condition(condition, debug);
//            ConditionsLexer lexer = new ConditionsLexer(CharStreams.fromString(condition));
//            lexer.addErrorListener(new GrammarErrorListener());
//
//            if (debug) {
//                // we can see parsed tokens only once !
//                for (Token tok : lexer.getAllTokens()) {
//                    System.err.println("token: " + tok.getText() + "\t" + tok.getType() + "\t" + lexer.getVocabulary().getSymbolicName(tok.getType()));
//                }
//                lexer = new ConditionsLexer(CharStreams.fromString(condition));
//                lexer.addErrorListener(new GrammarErrorListener());
//
//            }
//            CommonTokenStream tokens = new CommonTokenStream(lexer);
//            ConditionsParser parser = new ConditionsParser(tokens);
//            parser.addErrorListener(new GrammarErrorListener());
//            tree = parser.prog(); // parser
        } catch (ParseCancellationException e) {
            throw new ConllException(e.getMessage());
        }
    }

    public boolean evaluate(Map<String, Set<String>> wordlists, ConllWord cword) throws Exception {
        CEvalVisitor eval = new CEvalVisitor(cword, wordlists);
        boolean rtc = eval.visit(tree);
        //System.err.println("rtc " + rtc);
        return rtc;
    }

    //List<ParseTree> parsetrees;
    /**
     * load and prepare a list of validation lines
     */
//    public CheckConditions(List<String>lines) {
//         parsetrees = new ArrayList<>(); 
//         for (String condition : conditions) {
//            ConditionsLexer lexer = new ConditionsLexer(CharStreams.fromString(condition));
//            lexer.addErrorListener(new GrammarErrorListener());
//            CommonTokenStream tokens = new CommonTokenStream(lexer);
//            ConditionsParser parser = new ConditionsParser(tokens);
//            parser.addErrorListener(new GrammarErrorListener());
//            ParseTree tree = parser.prog(); // par
//            parsetrees.add(tree);
//         }
//         System.err.format("%d conditions loaded", parsetrees.size());
//    }
//    public boolean evaluate(ConllWord cword) {
//        // word must be part of a sentence and ConllSentence.maketrees() must have been called
//        CEvalVisitor eval = new CEvalVisitor(cword, null);
//        for (ParseTree tree : parsetrees) {
//        boolean rtc = eval.visit(tree);
//        }
//    }
    /* only compile a condition to be applied later */
    public static ParseTree parse_condition(String condition, boolean debug) {
        ConditionsLexer lexer = new ConditionsLexer(CharStreams.fromString(condition));
        lexer.addErrorListener(new GrammarErrorListener());

        if (debug) {
            // we can see parsed tokens only once !
            for (Token tok : lexer.getAllTokens()) {
                System.err.println("token: " + tok.getText() + "\t" + tok.getType() + "\t" + lexer.getVocabulary().getSymbolicName(tok.getType()));
            }
            lexer = new ConditionsLexer(CharStreams.fromString(condition));
            lexer.addErrorListener(new GrammarErrorListener());

        }
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConditionsParser parser = new ConditionsParser(tokens);
        parser.addErrorListener(new GrammarErrorListener());
        ParseTree tree = parser.prog(); // parser
        return tree;
    }

    /* evaluate a condition on a ConllWord and also stores lists for Form and Lemma: "filename": (words) */
    public static boolean evaluate_condition(ParseTree tree, Map<String, Set<String>> wordlists, ConllWord cword) throws Exception {
        CEvalVisitor eval = new CEvalVisitor(cword, wordlists);
        boolean rtc = eval.visit(tree);
        //System.err.println("rtc " + rtc);
        return rtc;
    }

    /* evaluate a condition on a ConllWord and also stores lists for Form and Lemma: "filename": (words) */
    public static boolean parse_end_evaluate_condition(String condition, Map<String, Set<String>> wordlists, ConllWord cword, boolean debug) throws Exception {
        ParseTree tree = parse_condition(condition, debug);
        return evaluate_condition(tree, wordlists, cword);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: Condition '<condition>' '<conllu word (spaces instead of tabs)>'");
        } else {

            ConditionsLexer lexer = new ConditionsLexer(CharStreams.fromString(args[0]));
            lexer.addErrorListener(new GrammarErrorListener());

            if (true) {
                // we can see parsed tokens only once !
                for (Token tok : lexer.getAllTokens()) {
                    System.err.println("token: " + tok.getText() + "\t" + tok.getType() + "\t" + lexer.getVocabulary().getSymbolicName(tok.getType()));
                }
                lexer = new ConditionsLexer(CharStreams.fromString(args[0]));
                lexer.addErrorListener(new GrammarErrorListener());
            }
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ConditionsParser parser = new ConditionsParser(tokens);
            parser.addErrorListener(new GrammarErrorListener());

            ParseTree tree = parser.prog(); // parse

            //ConllWord cword;
            ConllSentence csent;
            if (args.length == 1) {
                //cword = new ConllWord("1\trules\trule\tNOUN\tNNS\tNumber=Plur|Gender=Neut\t2\tnsubj\t_\tSpaceAfter=No", null, null);
                csent = new ConllSentence("1\trules\trule\tNOUN\tNNS\tNumber=Plur|Gender=Neut\t2\tnsubj\t_\t_\n"
                        + "2\tsleep\tsleep\tVERB\tNOUN\tNumber=Plur|Person=3\t0\troot\t_\tSpaceAfter=No", null);

            } else {
                csent = new ConllSentence(args[1].replaceAll(" +", "\t"), null);
            }
            csent.makeTrees(null);
            ConllWord cword = csent.getWord(1);
            ConllWord cword2 = csent.getWord(2);
            System.err.println(csent);
            CEvalVisitor eval = new CEvalVisitor(cword, null);
            boolean rtc = eval.visit(tree);
            System.err.println("rtc " + rtc);
        }
    }

    private Exception ConllException(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
