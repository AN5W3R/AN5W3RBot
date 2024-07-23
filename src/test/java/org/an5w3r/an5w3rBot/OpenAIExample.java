package org.an5w3r.an5w3rBot;

import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

public class OpenAIExample {
    private static final String API_KEY = "sk-bkEqLfbX0Wp5xGRp778fAc65289746409b4a9f1bB398A99f";
    private static final String BASE_URL = "https://free.gpt.ge/v1/";

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api);

        ChatMessage message = new ChatMessage("user", "Hello world");

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(message))
                .build();

        String completion = service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        System.out.println(completion);
    }
}
