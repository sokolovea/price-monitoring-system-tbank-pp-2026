package ru.tbank.pp.dto.yandex.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompletionOptions {
  boolean stream;
  double temperature;
  int maxTokens;
}
