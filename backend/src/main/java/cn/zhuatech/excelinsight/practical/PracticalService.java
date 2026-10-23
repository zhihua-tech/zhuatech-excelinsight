/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight.practical;

import static cn.zhuatech.excelinsight.practical.PracticalInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class PracticalService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "columns", "rows");
    var columns = rows(r, "columns", 1, 20);
    Set<String> colIds = new HashSet<>();
    for (var c : columns) {
      keys(c, "id", "type");
      unique(colIds, id(c, "id"));
      if (!Set.of("TEXT", "NUMBER", "DATE").contains(text(c, "type", 10)))
        throw new IllegalArgumentException("列类型错误");
    }
    var input = rows(r, "rows", 0, 200);
    Set<String> ids = new HashSet<>();
    for (var row : input) {
      keys(row, "id", "values");
      unique(ids, id(row, "id"));
      list(row, "values", columns.size(), columns.size());
    }
    List<Map<String, Object>> profiles = new ArrayList<>(), issues = new ArrayList<>();
    for (int index = 0; index < columns.size(); index++) {
      var col = columns.get(index);
      String type = col.get("type").toString();
      int missing = 0, invalid = 0, valid = 0;
      Set<String> distinct = new HashSet<>();
      BigDecimal sum = BigDecimal.ZERO, min = null, max = null;
      for (var row : input) {
        Object raw = list(row, "values", columns.size(), columns.size()).get(index);
        if (raw != null && (!(raw instanceof String) || ((String) raw).length() > 4000))
          throw new IllegalArgumentException("单元格须为4000字符内文本或null");
        String value = raw == null ? "" : ((String) raw).strip();
        if (value.isEmpty()) {
          missing++;
          continue;
        }
        try {
          Object parsed = value;
          if (type.equals("DATE")) parsed = date(value).toString();
          else if (type.equals("NUMBER")) {
            if (!value.matches("[+-]?[0-9]+(?:[.][0-9]+)?"))
              throw new IllegalArgumentException("数值格式错误");
            var n = decimal(Map.of("v", new BigDecimal(value)), "v", -1e12, 1e12);
            if (n.stripTrailingZeros().scale() > 6) throw new IllegalArgumentException("最多6位小数");
            parsed = n.stripTrailingZeros().toPlainString();
            sum = sum.add(n);
            min = min == null ? n : min.min(n);
            max = max == null ? n : max.max(n);
          }
          valid++;
          distinct.add(parsed.toString());
        } catch (IllegalArgumentException ex) {
          invalid++;
          issues.add(
              Map.of("row", row.get("id"), "column", col.get("id"), "issue", "INVALID_" + type));
        }
      }
      profiles.add(
          Map.of(
              "column",
              col.get("id"),
              "type",
              type,
              "valid",
              valid,
              "missing",
              missing,
              "invalid",
              invalid,
              "distinctValid",
              distinct.size(),
              "numericDefined",
              type.equals("NUMBER") && valid > 0,
              "minimum",
              min == null ? "" : min,
              "maximum",
              max == null ? "" : max,
              "mean",
              type.equals("NUMBER") && valid > 0
                  ? sum.divide(BigDecimal.valueOf(valid), 6, RoundingMode.HALF_UP)
                  : ""));
    }
    return Map.of(
        "rowCount", input.size(), "columns", profiles, "issues", issues, "fileParsed", false);
  }
}
