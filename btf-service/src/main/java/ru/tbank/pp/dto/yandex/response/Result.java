package ru.tbank.pp.dto.yandex.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Result {
  List<Alternative> alternatives;
  Usage usage;
  String modelVersion;
}
