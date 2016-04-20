/**
 * User: Administrator
 */
package com.jenkov.db.impl;

import java.util.StringTokenizer;

public class WordTokenizer {

    StringTokenizer tokenizer = null;

    public WordTokenizer(String memberName){
        String memberNameFirstLetterUpperCase =
                memberName.substring(0,1).toUpperCase() + memberName.substring(1, memberName.length());

        this.tokenizer = new StringTokenizer(memberNameFirstLetterUpperCase, "ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789", true);
    }


    public String nextWord(){
        if(!this.tokenizer.hasMoreTokens()) return null;
        String nextWordFirstLetter = this.tokenizer.nextToken();

        if(!this.tokenizer.hasMoreTokens()) return nextWordFirstLetter;
        String nextWordEnd         = this.tokenizer.nextToken();

        return nextWordFirstLetter + nextWordEnd;
    }

    public boolean hasMoreWords(){
        return this.tokenizer.hasMoreTokens();
    }

}
