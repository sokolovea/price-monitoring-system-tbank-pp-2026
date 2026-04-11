package ru.tbank.pp.dto.yandex.request;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tbank.pp.dto.yandex.Message;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YandexGptRequest {
  String modelUri;
  CompletionOptions completionOptions;
  List<Message> messages;
}
