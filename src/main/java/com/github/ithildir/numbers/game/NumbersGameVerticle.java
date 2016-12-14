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

package com.github.ithildir.numbers.game;

import com.github.ithildir.numbers.game.util.ConversationUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Random;

/**
 * @author Andrea Di Giorgi
 */
public class NumbersGameVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServer httpServer = vertx.createHttpServer();

		Router router = Router.router(vertx);

		Route route = router.route();

		route.handler(BodyHandler.create());

		route = router.route(HttpMethod.POST, "/actions");

		route.handler(this::_handle);

		httpServer.requestHandler(router::accept);

		httpServer.listen(
			_PORT,
			result -> {
				if (result.succeeded()) {
					startFuture.complete();
				}
				else {
					startFuture.fail(result.cause());
				}
			});
	}

	private void _handle(RoutingContext routingContext) {
		JsonObject requestJSON = routingContext.getBodyAsJson();

		JsonArray inputsJSON = requestJSON.getJsonArray("inputs");

		JsonObject inputJSON = inputsJSON.getJsonObject(0);

		String intent = inputJSON.getString("intent");

		if (intent.equals(ConversationUtil.INTENT_MAIN)) {
			_handleMain(routingContext.response());
		}
		else {
			_handleText(requestJSON, inputJSON, routingContext.response());
		}
	}

	private void _handleMain(HttpServerResponse httpServerResponse) {
		Random random = new Random();

		int number1 = random.nextInt(7) + 2;
		int number2 = random.nextInt(7) + 2;

		int answer = number1 * number2;

		ConversationUtil.ask(
			httpServerResponse, String.valueOf(answer),
			"What is " + number1 + " multiplied by " + number2 + "?",
			_NO_INPUT_PROMPTS);
	}

	private void _handleText(
		JsonObject requestJSON, JsonObject inputJSON,
		HttpServerResponse httpServerResponse) {

		JsonArray rawInputsJSON = inputJSON.getJsonArray("raw_inputs");

		JsonObject rawInputJSON = rawInputsJSON.getJsonObject(0);

		String query = rawInputJSON.getString("query");

		JsonObject conversationJSON = requestJSON.getJsonObject("conversation");

		int correctAnswer = Integer.parseInt(
			conversationJSON.getString("conversation_token"));

		int answer;

		try {
			answer = Integer.parseInt(query);
		}
		catch (NumberFormatException nfe) {
			ConversationUtil.ask(
				httpServerResponse, String.valueOf(correctAnswer),
				"That's not a number, say it again!", _NO_INPUT_PROMPTS);

			return;
		}

		String textToSpeech = "You're right, it is " + correctAnswer + "!";

		if (answer != correctAnswer) {
			textToSpeech = "You're wrong, it was " + correctAnswer + "!";
		}

		ConversationUtil.tell(httpServerResponse, textToSpeech);
	}

	private static final String[] _NO_INPUT_PROMPTS =
		new String[] {"Say something, please!", "Please say something!"};

	private static final int _PORT = 8080;

}