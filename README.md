# ExceptionAI Plugin Documentation
This plugin is free to use and helps Java developers to find alternative solutions to exceptions that are thrown in their Java applications. The plugin analyzes the stack trace output in the console and suggests possible solutions based on language models.

# Configuration
Before using the plugin, you need to configure it by setting your OpenAI API Key and Language Model. To configure the plugin, follow these steps:
* Click on **File > Settings > ExceptionAI Settings**</b> 
* In the <b>API Key</b> field, enter your OpenAI API key. <small>(Required)</small> You can create a free account at https://platform.openai.com/signup and get <b>$5</b> free credits.
* In the <b>Model</b> field, select the <b>Language Model</b> you want to use. <small>(Required)</small> See the documentation about the Models: https://platform.openai.com/docs/api-reference/models/list
* In the <b>Max Tokens</b> field, you can choose the maximum number of tokens to generate in the chat completion. The total length of input tokens and generated tokens is limited by the model's context length. Default is set to <b>500</b> and minimum allowed value in this plugin is <b>100</b>. If your answers are not completed, you might want to increase this value.
* In the <b>Temperature</b> field, you can choose what sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. Default value is set to <b>1.0</b>
* Click on the <b>OK</b> button to save your changes.

You can check OpenAI documentation to find out more: https://platform.openai.com/docs/api-reference/chat

# Usage
Once you have configured the plugin, you can start using it to find alternative solutions to Exceptions that are thrown in your Java applications. To use the plugin, follow these steps:</p>
* Run your Java application in the IntelliJ IDEA IDE.
* If an Exception is thrown, look for the exception message in the console.
* Click on the exception message in the console.
* The source Java file that caused the Exception will be opened in the Code Editor and the line will be highlighted.
* A pop-up window will appear with alternative solutions to the Exception.
* You can easily <b>Turn On</b> / <b>Turn Off</b> the plugin from the **Tools** menu if you want to navigate through the stack trace without triggering the ExceptionAI suggestion function. To make the *"Toggle"* function work, you'll need to re-run your application.

Note that in order for the plugin to suggest solutions, you need to have an internet connection to access the OpenAI API.

# Conclusion
Congratulations! You now know how to configure, and use the ExceptionAI plugin. Happy coding!
  
# Donations
If you find the ExceptionAI Plugin useful, you can support the developer by making a donation using PayPal.
I have decided to donate all the money received through donations for this plugin to support the victims and their families after devastating earthquake in Turkey.
Use my <a href="https://www.paypal.com/donate/?business=Z8BCRWQ99B99S&no_recurring=0&item_name=Donations+help+to+support+the+ongoing+development+of+the+ExceptionAI+plugin.&currency_code=USD">PayPal link</a> to send your donations.

# Support

If you encounter any issues, have a feature request, or any questions about ExceptionAI plugin, I am here to help! Please feel free to reach out to me through the following channels:

* <b>Email:</b> You can send an email to yonetmen@gmail.com with a detailed description of your issue, request, or question. Our team will get back to you as soon as possible.

* <b>GitHub:</b> You can also submit an issue or request on this GitHub repository at https://github.com/yonetmen/ExceptionAI. This is an excellent way to track the progress of your request and engage in discussions with the developer and other users. I encourage you to search the existing issues to see if someone else has already submitted a similar request or encountered the same problem before creating a new one.

Your feedback and suggestions are invaluable to me, and I am committed to providing support and continuously improving this plugin. Thank you for trying out ExceptionAI plugin!
