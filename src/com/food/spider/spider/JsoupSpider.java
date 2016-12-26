package com.food.spider.spider;import com.alibaba.fastjson.JSON;import com.food.spider.model.Point;import com.food.spider.model.Shop;import org.jsoup.Jsoup;import org.jsoup.nodes.Document;import org.jsoup.nodes.Element;import java.io.IOException;import java.util.ArrayList;import java.util.HashMap;import java.util.ListIterator;import java.util.Map;import java.util.regex.Matcher;import java.util.regex.Pattern;/** * Created by user on 16-12-3. */public class JsoupSpider implements ISpider {    /**     * 获取网页的内容 返回的是带有标签的html     *     * @param url     * @return     * @throws IOException     */    @Override    public String getHTMLBody(String url) {        String body = "";        try {            Document document = Jsoup.connect(url)                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")                    .header("Accept-Encoding", "gzip, deflate, sdch")                    .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2")                    .header("Cache-Control", "max-age=0")                    .header("Connection", "keep-alive")                    .header("Cookie", "_hc.v=fa6bcb56-a943-f95e-7247-3c50242870d5.1480728569; PHOENIX_ID=0a010818-158c24d2ca5-379212; __utma=205923334.628316922.1480733186.1480733186.1480733186.1; __utmc=205923334; __utmz=205923334.1480733186.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); s_ViewType=10; __mta=46617368.1480728756162.1480728756162.1480734449278.2; JSESSIONID=F1BBBEF43B780366EABE578A43F56656; aburl=1; cy=2; cye=beijing")                    .header("Host", "www.dianping.com")                    .header("Upgrade-Insecure-Requests", "1")                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36")                    .timeout(3000)                    .get();            body = document.html();        } catch (IOException ex) {            System.out.print(ex.getMessage());        }        //打印页面内容        return body;    }    /**     * 解析列表页html 返回详细页面的link     *     * @param html     * @return     */    @Override    public ArrayList<String> getLinkList(String html) {        ArrayList<String> linkList = new ArrayList<String>();        Document document = Jsoup.parse(html);        ListIterator<Element> listIterator = document.select("div.pic a[href]").listIterator();        while (listIterator.hasNext()) {            Element element = listIterator.next();            if (element.hasAttr("href")) {                linkList.add(element.attr("href"));            }        }        return linkList;    }    /**     * 解析详细页html 返回结果对象     *     * @param html     * @return     */    @Override    public Object getObject(String html) {        String name = "";        String address = "";        String shopHours = "";        ArrayList<String> tels = new ArrayList<String>();        Point point = new Point();        int consumptionPerPerson = 0;        float flavor = 0.0f;        float environmentalScience = 0.0f;        float service = 0.0f;        Document document = Jsoup.parse(html);        //名称        name = getShopName(document);        //地址        address = getAddress(document);        //电话        tels = getTelList(document);        //经纬度        point = getPoint(document);        //营业时间        shopHours = getShopHours(document);        //人均消费        consumptionPerPerson = getConsumptionPerPerson(document);        //口味        flavor = getFlavor(document);        //环境        environmentalScience = getEnvironmentalScience(document);        //服务        service = getService(document);        Shop shop = Shop.getNewStance(name, address, tels, point, shopHours, consumptionPerPerson                , flavor, environmentalScience, service);        return shop;    }    @Override    public Map<String, String> getLinkListOfCategory(String url) {        return null;    }    @Override    public Map<String, String> getLinkListOfDistrict(String url) {        Map<String, String> map = new HashMap<>();        String html = this.getHTMLBody(url);        if (html.equals(""))            return map;        Document document = Jsoup.parse(html);        String divStr = document.select("script.J_auto-load").html();        Document div = Jsoup.parse(divStr);        ListIterator<Element> elementListIterator = div.select("div.fpp_business li a").listIterator();        while (elementListIterator.hasNext()) {            Element element = elementListIterator.next();            map.put(element.html(), element.attr("href"));        }        return map;    }    //region 解析方法    /**     * 获取名字     *     * @param document     * @return     */    private String getShopName(Document document) {        return document.select("meta[itemprop='name']").attr("content");    }    /**     * 获取地址     *     * @param document     * @return     */    private String getAddress(Document document) {        return document.select("span[itemprop='street-address']").first().html();    }    /**     * 获取电话     *     * @param document     * @return     */    private ArrayList<String> getTelList(Document document) {        ArrayList<String> telList = new ArrayList<>();        ListIterator<Element> listIterator = document.select("span[itemprop='tel']").listIterator();        while (listIterator.hasNext()) {            Element element = listIterator.next();            telList.add(element.html());        }        return telList;    }    /**     * 获取经纬度     *     * @param document     * @return     */    private Point getPoint(Document document) {        //{lng:116.460324,lat:39.927624}        Point point = null;        String scriptStr = document.select("#aside script").html();        Pattern p = Pattern.compile("\\{lng\\:\\d+\\.\\d+\\ ?, ?lat\\:\\d+\\.\\d+\\}");        Matcher m = p.matcher(scriptStr);        if (m.find()) {            point = JSON.parseObject(m.group(), Point.class);        }        return point;    }    /**     * 获取营业时间     *     * @param document     * @return     */    private String getShopHours(Document document) {        return document.select("div.J-other span.item").html();    }    /**     * 人均消费     *     * @param document     * @return     */    private int getConsumptionPerPerson(Document document) {        int consumptionPerPerson = 0;        //人均：165元        String content = document.select("div.brief-info span.item").get(1).html();        Pattern p = Pattern.compile("\\d{1,10}");        Matcher m = p.matcher(content);        if (m.find()) {            consumptionPerPerson = Integer.parseInt(m.group());        }        return consumptionPerPerson;    }    /**     * 口味     *     * @param document     * @return     */    private float getFlavor(Document document) {        float flavor = 0.0f;        //口味：8.7        String content = document.select("div.brief-info span.item").get(2).html();        Pattern p = Pattern.compile("\\d{1}\\.?\\d?");        Matcher m = p.matcher(content);        if (m.find()) {            flavor = Float.parseFloat(m.group());        }        return flavor;    }    /**     * 环境     *     * @param document     * @return     */    private float getEnvironmentalScience(Document document) {        float environmentalScience = 0.0f;        //环境：9.2        String content = document.select("div.brief-info span.item").get(3).html();        Pattern p = Pattern.compile("\\d{1}\\.?\\d?");        Matcher m = p.matcher(content);        if (m.find()) {            environmentalScience = Float.parseFloat(m.group());        }        return environmentalScience;    }    /**     * 服务     *     * @param document     * @return     */    private float getService(Document document) {        float service = 0.0f;        //服务：9.0        String content = document.select("div.brief-info span.item").get(3).html();        Pattern p = Pattern.compile("\\d{1}\\.?\\d?");        Matcher m = p.matcher(content);        if (m.find()) {            service = Float.parseFloat(m.group());        }        return service;    }    //endregion}