package com.yuntun.calendar_sys.controller.wechat;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.Temp;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.bean.TempBean;
import com.yuntun.calendar_sys.model.code.TempCode;
import com.yuntun.calendar_sys.model.dto.TempDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ITempService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/wechat/open/temp")
public class TempController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    ITempService iTempService;

    @GetMapping("/list")
    public Result<Object> getList(Integer pageSize, Integer pageNo, TempDto dto) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<Temp> tempPage;
        try {
            tempPage = iTempService.page(
                    new Page<Temp>()
                            .setSize(pageSize)
                            .setCurrent(pageNo),
                    new QueryWrapper<Temp>()
                            .le("publish_time", LocalDate.now())
                            .orderByDesc("publish_time")
            );
        } catch (Exception e) {
            log.error("查询官方图文模板列表失败");
            throw new ServiceException(TempCode.LIST_TEMP_FAILURE);
        }

        List<TempBean> tempBeanList = tempPage.getRecords()
                .parallelStream()
                .map(this::getTempBean)
                .collect(Collectors.toList());

        RowData<TempBean> data = RowData.of(TempBean.class)
                .setRows(tempBeanList)
                .setTotal(tempPage.getTotal())
                .setTotalPages(tempPage.getTotal());
        return Result.ok(data);
    }

    @GetMapping("/list/months")
    public Result<RowData<JSONObject>> getListMonth(Integer pageSize, Integer pageNo, String date) {
        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");


        QueryWrapper<Temp> queryWrapper = new QueryWrapper<Temp>()
                .le("publish_time", LocalDate.now())
                .orderByDesc("publish_time");

        //指定月份
        if (EptUtil.isNotEmpty(date)) {

            LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(date);
            LocalDateTime dateTimeEnd = dateTimeStart.plus(1, ChronoUnit.MONTHS);

            LocalDateTime start = dateTimeStart.with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime end = dateTimeEnd.with(TemporalAdjusters.firstDayOfMonth());

            queryWrapper
                    .ge("publish_time", start.toLocalDate())
                    .lt("publish_time", end.toLocalDate())
            ;
        }

        Page<Temp> page = new Page<Temp>().setSize(pageSize).setCurrent(pageNo);
        IPage<Temp> tempIPage;
        try {
            tempIPage = iTempService.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(TempCode.LIST_MONTH_FAILURE);
        }

        List<Temp> records = tempIPage.getRecords();
        Map<Integer, List<TempBean>> collect = records.parallelStream()
                .map(this::getTempBean)
                .collect(Collectors.groupingBy(i -> i.getPublishTime().getMonthValue()));

        List<JSONObject> list = collect.entrySet().parallelStream()
                .map(i -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("rows", i.getValue());
                    jsonObject.put("month", i.getKey());
                    return jsonObject;
                }).collect(Collectors.toList());

        list = list.stream().sorted(
                (o1, o2) -> o2.getInteger("month") < (o1.getInteger("month")) ? 1 : -1
        ).collect(Collectors.toList());


        RowData<JSONObject> data = RowData.of(JSONObject.class)
                .setRows(list)
                .setTotal(tempIPage.getTotal())
                .setTotalPages(tempIPage.getTotal());
        log.info("返回的官方图文月份列表：{}", list);
        return Result.ok(data);
    }

    public static void main(String[] args) {
        List<JSONObject> list = JSONArray.parseArray(
                "[{\"month\":12,\"rows\":[{\"createTime\":\"2020-12-31T00:00:00\",\"id\":31,\"lunar\":\"十七\",\"publishTime\":\"2020-12-31\",\"tempContent\":[\"阿弥陀佛能守护你\",\"得大智慧 摆脱烦恼\",\"让你勇往直前\",\"一生顺利 吉祥如意\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-24/c4d0976b-eddb-44c7-bb4a-3871f4fa1c24.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"戌狗 亥猪\"},{\"createTime\":\"2020-12-30T00:00:00\",\"id\":30,\"lunar\":\"十六\",\"publishTime\":\"2020-12-30\",\"tempContent\":[\"不动明王是理性的象征\",\"能让你知错知过 并能改善\",\"把握人生中的每一次机会\",\"愿你\",\"事业有成 家庭幸福 吉祥如意\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-24/f3efddad-0d7e-4bd2-8186-7440c4090230.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"酉鸡\"},{\"createTime\":\"2020-12-29T00:00:00\",\"id\":29,\"lunar\":\"十五\",\"publishTime\":\"2020-12-29\",\"tempContent\":[\"大日如来是光明理智的象征\",\"能带来光明理智 除妖辟邪\",\"愿你通天地之灵气 \",\"取万物之精华\",\"勇往直前 光明快乐\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-24/fdb3cad1-9534-4ec5-97de-d2e852a00a06.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"未羊 申猴\"},{\"createTime\":\"2020-12-28T00:00:00\",\"id\":28,\"lunar\":\"十四\",\"publishTime\":\"2020-12-28\",\"tempContent\":[\"大势至菩萨带来智慧之光\",\"使你跟随佛光前进\",\"愿你一帆风顺 事业有成\",\"逢凶化吉 吉祥如意\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/f10f2c39-a0d4-4c6b-8d17-2f18f8988aec.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"午马\"},{\"createTime\":\"2020-12-27T00:00:00\",\"id\":27,\"lunar\":\"十三\",\"publishTime\":\"2020-12-27\",\"tempContent\":[\"普贤菩萨是礼德和大行愿的象征\",\"敬俸普贤菩萨\",\"使你实现最大愿望\",\"增加领导者权威远离小人\",\"愿你幸福美满\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/b9ebf765-037c-4fec-a001-ae0e047e1c28.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"辰龙 巳蛇\"},{\"createTime\":\"2020-12-26T00:00:00\",\"id\":26,\"lunar\":\"十二\",\"publishTime\":\"2020-12-26\",\"tempContent\":[\"文殊菩萨是大智慧的象征\",\"能助 学业有成  福禄双增\",\"增财增福 心想事成\",\"愿你\",\"充满智慧 飞黄腾达\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/ce0a0b64-b688-4414-a500-f4dbc1eb90af.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"卯兔\"},{\"createTime\":\"2020-12-25T00:00:00\",\"id\":25,\"lunar\":\"十一\",\"publishTime\":\"2020-12-25\",\"tempContent\":[\"虚空菩萨是诚实富有的象征\",\"不虚不空是佛界的财神\",\"愿你财路畅通无阻 \",\"生财聚财 贵人相助\",\"远离小人 人财俱旺\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/50e98f1c-73b2-48b9-9298-7f03fde7d3e9.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"丑牛 寅虎\"},{\"createTime\":\"2020-12-24T00:00:00\",\"id\":24,\"lunar\":\"初十\",\"publishTime\":\"2020-12-24\",\"tempContent\":[\"千手观音菩萨是大慈悲的象征\",\"运气旺时能使你更辉煌\",\"运气低落时能消除障碍化解灾难\",\"愿你\",\"一生幸福 平安吉祥\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/5ea801ae-310c-4721-a6aa-bf11d2363fb8.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"子鼠 \"},{\"createTime\":\"2020-12-23T00:00:00\",\"id\":23,\"lunar\":\"初九\",\"publishTime\":\"2020-12-23\",\"tempContent\":[\"闻佛无量德志而不倦\",\"以智慧剑 破烦恼贼\",\"出阴界入 负荷众生 永使解脱\",\"不坏威仪法而能随俗\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-23/ff28d383-0814-42b9-a456-828231c0ae00.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"智慧剑\"},{\"createTime\":\"2020-12-22T00:00:00\",\"id\":22,\"lunar\":\"初八\",\"publishTime\":\"2020-12-22\",\"tempContent\":[\"天一生水 地六成之\",\"天二生火 地七成之\",\"天三生木 地八成之\",\"天四生金 地九成之\",\"天五生土 地十成之\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-22/44e55ece-ab39-4ffe-8dbd-b0f35076fda8.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"乾坤\"},{\"createTime\":\"2020-12-21T00:00:00\",\"id\":21,\"lunar\":\"初七\",\"publishTime\":\"2020-12-21\",\"tempContent\":[\"是谁在寒夜里期待着\",\"不期而遇的温暖\",\"北方的水饺\",\"南方的汤圆\",\"还有东来西去的星辰大海\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-21/c0d405ee-2e39-4fe4-9090-6ab670841ccd.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"冬至 吉祥\"},{\"createTime\":\"2020-12-20T00:00:00\",\"id\":20,\"lunar\":\"初六\",\"publishTime\":\"2020-12-20\",\"tempContent\":[\"你是谁的的女儿\",\"又是谁的母亲\",\"午夜的寒风 送来一种香\",\"那是你我都熟悉的味道\",\"生命不已 奋斗不息\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-20/58340895-e894-4900-be9d-56968edce2f4.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"生活 味道\"},{\"createTime\":\"2020-12-19T00:00:00\",\"id\":19,\"lunar\":\"初五\",\"publishTime\":\"2020-12-19\",\"tempContent\":[\"都说要逃离北上广深\",\"可是谁都未曾离开\",\"人潮人海中 又见到了你\",\"梦想 就在路上\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-18/80303597-36b1-42bf-a768-16c6e2462481.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"在 路上\"},{\"createTime\":\"2020-12-18T00:00:00\",\"id\":18,\"lunar\":\"初四\",\"publishTime\":\"2020-12-18\",\"tempContent\":[\"未知何时开始对周五的期盼淡了\",\"可能是城市里的已婚单身多了\",\"胡适之说\",\"岂不爱自由 此意无人晓\",\"情愿不自由 也就自由了\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-17/d16aa840-5824-46a6-968d-18ba4d299ebb.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"九九六\"},{\"createTime\":\"2020-12-17T00:00:00\",\"id\":17,\"lunar\":\"初四\",\"publishTime\":\"2020-12-17\",\"tempContent\":[\"冬夜的孤山 在等雪飘\",\"盼着白的自在 飘来黑的香 \",\"那是懂你的纳咖啡\",\"在一声不吭的\",\"暖着你的梦想\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-15/96f21c1b-5ccb-4347-b298-2e4206eee437.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周四 纳咖啡\"},{\"createTime\":\"2020-12-16T00:00:00\",\"id\":16,\"lunar\":\"初三\",\"publishTime\":\"2020-12-16\",\"tempContent\":[\"无聊公司还是公司无聊？非也！\",\"你我皆凡人就不要想着跳出五行\",\"唯有在平凡的日子里 坚守初心\",\"你就会是夜空中最亮的星\",\"正如马克斯的无聊公司\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-15/9b92de4a-7d93-459d-9492-5f1bbe292521.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周三 无聊\"},{\"createTime\":\"2020-12-15T00:00:00\",\"id\":15,\"lunar\":\"初二\",\"publishTime\":\"2020-12-15\",\"tempContent\":[\"天之道 损有余而补不足\",\"人之道 损不足而益有余\",\"都说人格是平等的\",\"但地位呢 王侯将相宁有种乎\",\"加油！Are you OK?\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-15/4e5fd336-0b45-45ef-a0cd-302af794d3af.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周二 加油\"},{\"createTime\":\"2020-12-14T00:00:00\",\"id\":14,\"lunar\":\"初一\",\"publishTime\":\"2020-12-14\",\"tempContent\":[\"初六 履霜 坚冰至\",\"积不善之家 必有余殃\",\"积善之家 必有余庆\",\"只要谨小慎微 盖言顺也\",\"降温了 愿你暖和\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-13/8fe988ae-a681-4fcb-9a67-79d566a3041b.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周一 吉祥\"},{\"createTime\":\"2020-12-13T00:00:00\",\"id\":13,\"lunar\":\"三十\",\"publishTime\":\"2020-12-13\",\"tempContent\":[\"大衍之数五十（五）\",\"其用四十有九遁去其一（六）\",\"留给世人的  何止是一线天机\",\"等风来 不如追风去\",\"不懈努力 天机也助你 加油！\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-12/148cdc6f-738c-405f-8e9b-01bf2bbb9ade.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周末 快乐\"},{\"createTime\":\"2020-12-12T00:00:00\",\"id\":12,\"lunar\":\"廿九\",\"publishTime\":\"2020-12-12\",\"tempContent\":[\"不恨年华去也 只恐少年心事\",\"强半为消磨\",\"愿替众生病 稽首礼维摩\",\"落叶也是有理想的 何况是你我\",\"周六快乐\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-12/4d8750b4-c2c3-49d7-86b8-42e1edecdb76.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"周六 快乐\"},{\"createTime\":\"2020-12-11T00:00:00\",\"id\":10,\"lunar\":\"廿八\",\"publishTime\":\"2020-12-11\",\"tempContent\":[\"不知周之梦为蝴蝶与\",\"蝴蝶之梦为周与\",\"周与梦 则有分矣 此之谓物化\",\"庄子说的是理想和现实吗？\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-10/df282605-a6cb-4a7c-8142-37e0a76ed3b2.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"早安 蝴蝶\"},{\"createTime\":\"2020-12-10T00:00:00\",\"id\":9,\"lunar\":\"廿七\",\"publishTime\":\"2020-12-10\",\"tempContent\":[\"日月之行 若出其中\",\"星汉灿烂 若出其里\",\"霍金脑海中有果壳\",\"曹操眼眸里有宇宙\",\"幸甚至哉 歌以咏志   \"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-09/20b7b069-3347-44ff-a555-79a5d77b30b6.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"早安  果壳\"},{\"createTime\":\"2020-12-09T00:00:00\",\"id\":8,\"lunar\":\"廿六\",\"publishTime\":\"2020-12-09\",\"tempContent\":[\"远上寒山石径斜白云深处有人家\",\"停车坐爱枫林晚霜叶红于二月花\",\"努力奋斗的前行路上\",\"偶尔缓一缓 你会发现别样的美\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-08/84c6d338-21be-4968-bc02-91d700120c93.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"山行\"},{\"createTime\":\"2020-12-08T00:00:00\",\"id\":5,\"lunar\":\"廿五\",\"publishTime\":\"2020-12-08\",\"tempContent\":[\"东南形胜三吴都会钱塘自古繁华\",\"市列珠玑户盈罗绮竞奢豪\",\"重湖叠巘清嘉\",\"有三秋桂子十里荷花\",\"异日图将好景归去凤池夸\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-08/4f53187d-de3e-4e0a-9a56-bf921a429e94.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"望海潮\"},{\"createTime\":\"2020-12-07T00:00:00\",\"id\":6,\"lunar\":\"廿四\",\"publishTime\":\"2020-12-07\",\"tempContent\":[\"一花一世界\",\"一叶一菩提\",\"一书一哲理\",\"一诗一情怀\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-08/2226f917-5cf6-419a-bedf-b1b5561cab21.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"感悟\"},{\"createTime\":\"2020-12-01T00:00:00\",\"id\":7,\"lunar\":\"十八\",\"publishTime\":\"2020-12-01\",\"tempContent\":[\"为什么我眼里常含泪水\",\"因为我对这片土地爱得深沉\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2020-12-08/2bedff58-fceb-4f04-9dc7-8a2d7d93d3df.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"爱\"}]}, {\"month\":1,\"rows\":[{\"createTime\":\"2021-01-06T00:00:00\",\"id\":34,\"lunar\":\"廿三\",\"publishTime\":\"2021-01-06\",\"tempContent\":[\"横看成岭侧成峰\",\"远近高低各不同\",\"汝心若爱\",\"看什么都美\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2021-01-05/ed87447a-6030-4066-aab6-c19398a79059.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"早安\"},{\"createTime\":\"2021-01-05T00:00:00\",\"id\":33,\"lunar\":\"廿二\",\"publishTime\":\"2021-01-05\",\"tempContent\":[\"小寒时处二三九\",\"天寒地冻冷到抖\",\"要降温了\",\"汝心还暖和否？\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2021-01-05/5ddc9fef-abc3-4052-a83d-a9a6e95cf678.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"小寒 吉祥\"},{\"createTime\":\"2021-01-01T00:00:00\",\"id\":32,\"lunar\":\"十八\",\"publishTime\":\"2021-01-01\",\"tempContent\":[\"2021\",\"祝福你\",\"所求皆如愿\",\"所行皆坦途\"],\"tempId\":1,\"tempPicUrl\":\"https://file.16273849510.com/group1/cl_mini_app/2021-01-01/237e030e-6693-4741-8b6e-9b1df9eb6292.jpg?download=0\",\"tempSource\":\"官方\",\"tempTitle\":\"元旦 快乐\"}]}]",
                JSONObject.class
        );
        list = list.stream().sorted(
                (o1, o2) -> o2.getInteger("month") < (o1.getInteger("month")) ? 1 : -1
        ).collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * 具体某月的心语列表30条
     */
    @GetMapping("/list/limit30")
    public Result<Object> getTempList30(String date) {

        ErrorUtil.isStringEmpty(date, "日期");

        LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(date);

        //查询前15条
        List<Temp> TempListPrev = iTempService.list(
                new QueryWrapper<Temp>()
                        .orderByDesc("publish_time")
                        .ge("publish_time", dateTimeStart)
                        .le("publish_time", LocalDate.now())
                        .last("limit 15")

        );
        //查询后14条
        List<Temp> TempListNext = iTempService.list(
                new QueryWrapper<Temp>()
                        .orderByDesc("publish_time")
                        .lt("publish_time", dateTimeStart)
                        .le("publish_time", LocalDate.now())
                        .last("limit 14")

        );
        ArrayList<Temp> Temp = new ArrayList<>();
        Temp.addAll(TempListPrev);
        Temp.addAll(TempListNext);
        List<TempBean> collect = Temp.parallelStream()
                //过滤掉不属于当月的数据
                .filter(i -> i.getCreateTime().getMonthValue() == (dateTimeStart.getMonthValue()))
                .map(this::getTempBean)
                .collect(Collectors.toList());
        return Result.ok(collect);
    }


    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") Integer id) {

        ErrorUtil.isNumberValueLt(id, 0, "参数");
        try {
            Temp temp = iTempService.getById(id);
            if (EptUtil.isNotEmpty(temp)) {
                TempBean tempBean = getTempBean(temp);
                return Result.ok(tempBean);
            }
            return Result.error(TempCode.DETAIL_TEMP_FAILURE);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常:", e);
            throw new ServiceException(TempCode.DELETE_TEMP_FAILURE);
        }
    }

    private TempBean getTempBean(Temp temp) {
        TempBean tempBean = new TempBean();
        String[] split = temp.getTempContent().split(HeartWordsConstant.CONTENT_DELIMITER);
        BeanUtils.copyProperties(temp, tempBean);
        tempBean.setTempContent(Arrays.asList(split));
        tempBean.setCreateTime(temp.getPublishTime().atStartOfDay());
        return tempBean;
    }

}
