/**
 * Copyright (c) 2016 Andrea Di Giorgi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.ithildir.numbers.game.util;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Andrea Di Giorgi
 */
public class ConversationUtil {

	public static final String INTENT_MAIN = "assistant.intent.action.MAIN";

	public static final String INTENT_TEXT = "assistant.intent.action.TEXT";

	public static void ask(
		HttpServerResponse httpServerResponse, String conversationToken,
		String initialPrompt, String[] noInputPrompts) {

		httpServerResponse.putHeader(
			HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
		httpServerResponse.putHeader("Google-Assistant-API-Version", "v1");

		JsonObject responseJSON = new JsonObject();

		if (conversationToken != null) {
			responseJSON.put("conversation_token", conversationToken);
		}

		responseJSON.put("expect_user_response", true);
		responseJSON.put(
			"expected_inputs",
			JsonUtil.getArray(
				_getExpectedInputJSON(initialPrompt, noInputPrompts)));

		httpServerResponse.end(responseJSON.encode());
	}

	public static void tell(
		HttpServerResponse httpServerResponse, String textToSpeech) {

		httpServerResponse.putHeader(
			HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
		httpServerResponse.putHeader("Google-Assistant-API-Version", "v1");

		JsonObject responseJSON = new JsonObject();

		JsonObject finalResponseJSON = new JsonObject();

		finalResponseJSON.put("speech_response", _getPromptJSON(textToSpeech));

		responseJSON.put("final_response", finalResponseJSON);

		httpServerResponse.end(responseJSON.encode());
	}

	private static JsonObject _getExpectedInputJSON(
		String initialPrompt, String[] noInputPrompts) {

		JsonObject expectedInputJSON = new JsonObject();

		JsonObject inputPromptJSON = new JsonObject();

		inputPromptJSON.put(
			"initial_prompts",
			JsonUtil.getArray(_getPromptJSON(initialPrompt)));

		if (noInputPrompts.length > 0) {
			JsonArray noInputPromptsJSON = new JsonArray();

			for (String noInputPrompt : noInputPrompts) {
				noInputPromptsJSON.add(_getPromptJSON(noInputPrompt));
			}

			inputPromptJSON.put("no_input_prompts", noInputPromptsJSON);
		}

		expectedInputJSON.put("input_prompt", inputPromptJSON);

		expectedInputJSON.put(
			"possible_intents", _getPossibleIntentsJSON(INTENT_TEXT));

		return expectedInputJSON;
	}

	private static JsonArray _getPossibleIntentsJSON(String intent) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("intent", intent);

		return JsonUtil.getArray(jsonObject);
	}

	private static JsonObject _getPromptJSON(String textToSpeech) {
		JsonObject jsonObject = new JsonObject();

		jsonObject.put("text_to_speech", textToSpeech);

		return jsonObject;
	}

}