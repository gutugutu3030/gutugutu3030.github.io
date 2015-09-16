$(function () {
	$("#search").click(function () {
		console.log($("#searchStr"));
		var str=$("#searchStr").val();
		if(str==""){
			return;
		}
		if($("#sengok_ch").prop('checked')){
			window.open("http://www.sengoku.co.jp/mod/sgk_cart/search.php?multi="+str+"&cond8=and");
		}
		if($("#aiten_ch").prop('checked')){
			window.open("http://www.aitendo.com/product-list?keyword="+str+"&Submit=検索");
		}
		if($("#marutu_ch").prop('checked')){
			window.open("http://www.marutsu.co.jp/GoodsListNavi.jsp?path=&q="+str+"&searchbox=1");
		}
		if($("#switch_ch").prop('checked')){
			window.open("https://www.switch-science.com/catalog/list/?keyword="+str);
		}
		if($("#aki_ch").prop('checked')){
			window.open("http://akizukidenshi.com/catalog/goods/search.aspx?search=x&keyword="+str+"&image=%8C%9F%8D%F5");
		}
	});
});
