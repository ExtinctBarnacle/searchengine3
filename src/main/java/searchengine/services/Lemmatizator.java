package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lemmatizator {

    public static HashMap<String, Integer> getWordsCount(String text) throws IOException {
        HashMap<String, Integer> words = new HashMap<>();
        //String symbols = "!,.:;№-=";
        //text = text.substring(0,1000);
        String clearedText = text;
        /*for (int j=0;j<symbols.length();j++){
            String txt = symbols.substring(j,j+1);
            //System.out.println(txt);
            clearedText = text.replaceAll(symbols.substring(j,j+1), "");
        }*/
        clearedText = clearedText.replaceAll("[0-9]", " ");
        clearedText = clearedText.replaceAll("[̆?&/%—–‑+!,.:;№-]", " ");
        clearedText = clearedText.replaceAll("\\?", " ");
        clearedText = clearedText.replaceAll("\\»", " ");
        clearedText = clearedText.replaceAll("\\©", " ");
        clearedText = clearedText.replaceAll("\\«", " ");
        clearedText = clearedText.replaceAll("\"", " ");
        clearedText = clearedText.replaceAll("\\(", " ");
        clearedText = clearedText.replaceAll("\\)", " ");
        clearedText = clearedText.replaceAll("[a-zA-Z]", " ");
        clearedText = clearedText.toLowerCase();
        System.out.println(clearedText);
        String textArray[] = clearedText.split(" ");
        for (String i : textArray) {
            System.out.println(i);
            if (i.length() == 0) {
                continue;
            }
            LuceneMorphology luceneMorph = new RussianLuceneMorphology();
            List<String> wordBaseForms = new ArrayList<>();
            try {
                wordBaseForms = luceneMorph.getNormalForms(i);
            } catch (Exception e) {
                System.out.println("Слово " + i + " вызвало ошибку: " + e.toString());
                continue;
            }
            // wordBaseForms.forEach(System.out::println);
            List<String> wordInfo = luceneMorph.getMorphInfo(i);
            //wordInfo.forEach(System.out::println);
            String splittedWordInfo[] = wordInfo.get(0).split(" ");
            //System.out.println(splittedWordInfo[1]);
            //System.out.println(wordBaseForms.get(0));
            if (splittedWordInfo[1].equals("СОЮЗ") || splittedWordInfo[1].equals("МЕЖД") || splittedWordInfo[1].equals("ПРЕДЛ")) {
                continue;
            }
            Integer wordCount = words.get(wordBaseForms.get(0));
            if (wordCount == null) {
                wordCount = 1;
            } else {
                wordCount++;
            }
            words.put(wordBaseForms.get(0), wordCount);
        }
        return words;
    }
}