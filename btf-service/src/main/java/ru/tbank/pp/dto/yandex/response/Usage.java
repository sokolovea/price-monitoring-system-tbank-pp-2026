package ru.tbank.pp.dto.yandex.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Usage {
  String inputTextTokens;
  String completionTokens;
  String totalTokens;
  Object completionTokensDetails;
}
