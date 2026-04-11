package ru.tbank.pp.dto.yandex;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Message {
  String role;
  String text;
}
