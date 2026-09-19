/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight;

import cn.zhuatech.excelinsight.service.DataQualityGovernanceService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class DataQualityGovernanceServiceTests {
    private final DataQualityGovernanceService service = new DataQualityGovernanceService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void quarantinesUnapprovedSensitiveDataset() {
        var result = service.assess(new DataQualityGovernanceService.Request(
                "DS-01", 1000, 3, 2, 0, 4, true, true, false));
        assertThat(result.route()).isEqualTo("PRIVACY_APPROVAL");
        assertThat(result.publishAllowed()).isFalse();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void publishesGovernedCleanDataset() {
        var result = service.assess(new DataQualityGovernanceService.Request(
                "DS-02", 1000, 1, 1, 0, 0, true, true, false));
        assertThat(result.qualityGrade()).isEqualTo("A");
        assertThat(result.publishAllowed()).isTrue();
    }
}
