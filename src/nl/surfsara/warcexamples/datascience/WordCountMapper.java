/**
 * Copyright 2014 SURFsara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfsara.warcexamples.datascience;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jwat.common.HttpHeader;
import org.jwat.common.Payload;
import org.jwat.warc.WarcRecord;

import java.io.IOException;
import java.util.*;
import java.net.URI;

/**
 * Map function that from a WarcRecord extracts all words.
 * The resulting key, values: page URL, link.
 * 
 * @author jan.willem.van.velzen@gmail.com
 */
class WordCountMapper extends Mapper<LongWritable, WarcRecord, Text, Text> {
	private static enum Counters {
		CURRENT_RECORD, NUM_HTTP_RESPONSE_RECORDS
	}

	@Override
	public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());

		// Only process http response content. Note that the outlinks can also be found in the wat metadata.
		if ("application/http; msgtype=response".equals(value.header.contentTypeStr)) {
			// org.jwat.warc.WarcRecord is kind enough to also parse http headers for us:
			HttpHeader httpHeader = value.getHttpHeader();
			if (httpHeader == null) {
				// No header so we are unsure that the content is text/html: NOP
			} else {
				if (httpHeader.contentType != null && httpHeader.contentType.contains("text/html")) {
					// Note that if you really want to do this right; you should look at the character encoding as well.
					// We'll leave that as an exercise for you ;-).
					context.getCounter(Counters.NUM_HTTP_RESPONSE_RECORDS).increment(1);
					// Get the html payload
					Payload payload = value.getPayload();
					if (payload == null) {
						// NOP
					} else {
						String warcContent = IOUtils.toString(payload.getInputStreamComplete());
						if (warcContent == null && "".equals(warcContent)) {
							// NOP
						} else {
                            try {
                                //Remove all HTML from warcContent, right now it seems to empty the pages completely. Please do test for yourself
                                //warcContent = Jsoup.parse(warcContent).text();
                            } catch (Exception e) {
                            }

                            String targetURI = value.header.warcTargetUriStr;
                            //Write Word Count Mapping for domain, if valid URI
                            String targetDomain = getDomain(targetURI);

                            if(targetDomain != null) {

                                context.write(new Text(targetDomain), new Text(parseToString(countWords(warcContent))));

                                //Write Links
                                Document doc = Jsoup.parse(warcContent);
                                Elements links = doc.select("a");
                                for (Element link : links) {
                                    String absHref = link.attr("abs:href");
                                    String absHrefDomain = getDomain(absHref);
                                    // Omit nulls and empty strings
                                    if (absHrefDomain != null && absHref != null && !("".equals(absHref))) {
                                        context.write(new Text(targetDomain), new Text(absHrefDomain));
                                    }
                                }
                            }
						}
					}
				}
			}
		}
	}

    public static String getDomain(String URL) {
        
        try {
            URI uri = new URI(URL);
            String domain = uri.getHost();
            return domain.startsWith("www.")?domain.substring(4):domain;

        } catch (Exception e) {
            return null;
        }
    }


    public static HashMap<String, Integer> countWords( String s){
        HashMap<String, Integer> counter = new HashMap<String, Integer>();

        boolean word = false;
		String currentWord = "";
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
          char c =   s.charAt(i);
            boolean isLetter = Character.isLetter(c);
            // if the char is a letter, word = true.
            if (isLetter && i != endOfLine) {
                word = true;
                currentWord += c;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!isLetter && word) {
                word = false;
                Integer count =  counter.get(currentWord);
                counter.put(currentWord, count == null?1:count+1);
                currentWord = "";
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (isLetter && i == endOfLine) {
                currentWord += c;
                Integer count =  counter.get(currentWord);
                counter.put(currentWord, count == null?1:count+1);
                currentWord = "";
            }
        }
        return counter;
    }

    //these two will be reused in reducer code

    public static String parseToString(HashMap<String, Integer> m){
        String res = "Count:{";

        for(Map.Entry<String, Integer> e : m.entrySet()){
            res += e.getKey()+":"+e.getValue()+",";
        }

        //Remove last comma
        if(m.size()>0)
            res  = res.substring(0, res.length()-1);

        return res + "}";
    }

    public static HashMap<String, Integer> parseToMap(String s){
        HashMap<String, Integer> res = new HashMap<String, Integer>();

        if(!s.startsWith("Count:{"))
            return res;

        s = s.replace("Count:{", "").replace("}","");
        String[] counts = s.split(",");
        for(String count : counts){
            String[] parts = count.split(":");
            if(parts.length == 2){
                try{
                    res.put(parts[0], Integer.parseInt(parts[1]));
                }catch(Exception e){

                }
            }
        }

        return res;
    }

}
