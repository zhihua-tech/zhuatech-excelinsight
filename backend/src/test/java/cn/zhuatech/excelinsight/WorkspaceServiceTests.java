/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.excelinsight;

import cn.zhuatech.excelinsight.service.WorkspaceService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class WorkspaceServiceTests {
    private final WorkspaceService service = new WorkspaceService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void returnsReviewableDomainResult() {
        var request = new WorkspaceService.RunRequest("华东区逾期应收.xlsx", "财务应收筛选分析", 75, true, "演示上下文");
        var result = service.run(request);
        assertThat(result.status()).isEqualTo("REVIEW_READY");
        assertThat(result.executionMode()).isEqualTo("LOCAL_DEMO_PIPELINE");
        assertThat(result.insights()).hasSize(3);
        assertThat(result.actions()).hasSize(3);
        assertThat(result.providerPayload()).containsEntry("provider", "deepseek-compatible");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test
    void blocksDirectAutomationWithoutHumanReview() {
        var request = new WorkspaceService.RunRequest("华东区逾期应收.xlsx", "财务应收筛选分析", 60, false, "");
        var result = service.run(request);
        assertThat(result.status()).isEqualTo("HUMAN_REVIEW_REQUIRED");
        assertThat(result.warnings()).hasSizeGreaterThanOrEqualTo(2);
    }
}
