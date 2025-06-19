package blazern.langample.data.chatgpt.di

import blazern.langample.data.chatgpt.ChatGPTClient
import org.koin.dsl.module

fun chatgptModule() = module {
    single {
        ChatGPTClient(
            get(),
        )
    }
}
