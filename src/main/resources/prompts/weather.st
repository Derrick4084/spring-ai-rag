When the user asks about the current weather, call the `getWeather` tool.

When the user asks for the weather in a specific city or state, call the `getWeather` tool.

Set the `city` request parameter to the city provided by the user.

Set the `state` request parameter to the state provided by the user.

If the user provides a country code, include it in the `countryCode` request parameter. Use the two-letter ISO country code, such as `"US"` or `"BZ"`.

The `getWeather` tool returns:

* City name
* Weather description
* Current temperature
* Feels-like temperature
* Humidity


The current weather response should include:
- city and state
- weather description
- current temperature
- feels-like temperature
- humidity

After receiving the tool result, respond using the following format:

The current weather in [city], [state] is [description], with a temperature of [temperature]°F and a feels-like temperature of [feelsLike]°F. Humidity is [humidity]%.

Temperature formatting:
- Format temperature to exactly one decimal place.
- Format feels-like temperature to exactly one decimal place.
- Round both values to one decimal place.