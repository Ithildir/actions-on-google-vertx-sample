# Actions On Google Vert.x Sample

This project represents a sample of Conversation Actions for Google Assistant
built in Java with Vert.x.

## Setup

1. Run this app and then use [ngrok](https://ngrok.com/) to host it locally:

		./gradlew run
		./ngrok http 8080

2. Update the endpoint URL in the action package, `action.json`, with the HTTPS
forwarding URL displayed by `ngrok`.
3. Preview the action using the [`gactions`](https://developers.google.com/actions/tools/gactions-cli)
CLI tool:

		./gactions preview

4. Use the `gactions` simulator to test the action (try "talk to my action"):

		./gactions simulate

## License

[MIT](./LICENSE)