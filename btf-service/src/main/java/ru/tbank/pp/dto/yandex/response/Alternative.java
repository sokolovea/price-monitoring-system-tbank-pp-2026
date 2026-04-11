package ru.tbank.pp.dto.yandex.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.pp.dto.yandex.Message;


@Getter
@Setter
@AllArgsConstructor
public class Alternative {
  Message message;
  String status;
}
