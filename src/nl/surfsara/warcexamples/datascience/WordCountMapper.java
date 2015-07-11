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
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
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
						if (warcContent == null || "".equals(warcContent)) {
							// NOP
						} else {
                            String targetURI = value.header.warcTargetUriStr;

                            final HashMap<String, Integer> count = new HashMap<String, Integer>();
                            try {
                                countWords(count, warcContent);
                            } catch (Exception e) {

                            }
                            ArrayList<String> words = new ArrayList<String>();
                            for (Map.Entry<String, Integer> e : count.entrySet()) {
                                words.add(e.getKey());
                            }

                            Collections.sort(words, new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return count.get(s) - count.get(t1);
                                }
                            });

                            int max = Math.min(10, words.size());
                            for(int i = 0 ; i < max; i++){
                                String word = words.get(i);
                                context.write(new Text(targetURI), new Text(word + " : " + count.get(word)));
                            }
						}
					}
				}
			}
		}
	}


    public static void countWords(HashMap<String, Integer> counter, String s){

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
                currentWord +=c;
                word = false;
                int count = counter.get(currentWord);
                counter.put(currentWord, count+1);
                currentWord =  "";
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (isLetter && i == endOfLine) {
                currentWord +=c;
                int count = counter.get(currentWord);
                counter.put(currentWord, count+1);
                currentWord =  "";
            }
        }
    }


}
