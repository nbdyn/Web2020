$(function(){
	$("#publishBtn").click(publish);
});
$(function(){
	$("#publishBtn2").click(publish);
});

function publish() {
	$("#publish	Modal").modal("hide");

	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();
	var peopleNum=$("#people-num").val();
	var kind=$("#message-kind").val();
	var endDate=$("#message-date").val();

	// 发送异步请求
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content,"peopleNum":peopleNum,"kind":kind,"endDate":endDate},
		function(data) {
			data=$.parseJSON(data);
			//在提示框里显示返回消息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//2秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//成功的话刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);

		}
	);


}