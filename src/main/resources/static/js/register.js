/*发送验证码*/
var sendMsg = function () {
    /*未填写手机号无法发送信息*/
    if($("#input-phone").val()==""){
        return
    }
    $(".btn-getsms").click(function () {
        $(".btn-getsms-loading").show();
        $(this).hide();
        inSend();
    })
};
/*短信计时器*/
var inSend = function () {
    document.getElementsByClassName("count")[0].innerHTML = 30;//初始化为30秒
    var num = 30;
    var countSend = setInterval(function () {
        num==1?clearSend(countSend):"";
        num--;
        document.getElementsByClassName("count")[0].innerHTML = num;
    },1000);
};
var clearSend = function (name) {
    clearInterval(name);
    $(".btn-getsms-loading").hide();
    $(".btn-getsms").show();
};
/*input框添加监听事件*/
var addInputEvent = function () {
    $("input").change(function () {
        var index = $("input").index(this);
        $(".u-tip").eq(index).removeClass("con");
    })
};
var clearMsg = function () {
    $(".u-tip").click(function () {
        var index = $(".u-tip").index(this);
        $("input").eq(index).val("");
        $(".u-tip").eq(index).addClass("con");
        /*所有提示都清空*/
        $("table").eq(index).addClass("con");
        $(".succ").eq(index).addClass("con");
    })
};
/*输入校验*/
var accConfirm = function () {
    var index = $("input").index($("#input-account"));
    var table = $("table").eq(index)
    var suc = $(".succ")
    if($("#input-account").val()==""){
        table.removeClass()
        table.addClass("err popb")
        $(".b-p")[index].innerHTML = "请输入账号"
        return;
    }else {
        table.addClass("con");
        suc.eq(index).removeClass("con")
    }
    regisBtnUse();
};
var pwdConfirm = function () {
    var index = $("input").index($("#input-psd"));
    var table = $("table").eq(index)
    var suc = $(".succ")
    if($("#input-psd").val()==""){
        table.removeClass()
        table.addClass("err popb")
        $(".b-p")[index].innerHTML = "请输入密码"
        return;
    }else {
        table.addClass("con");
        suc.eq(index).removeClass("con")
    }
    //判断是否执行密码2校验
    $("#input-pw2").val()==""?(function(){return})():pwd2Confirm();
    regisBtnUse();
};
var pwd2Confirm = function () {
    var index = $("input").index($("#input-pw2"));
    var table = $("table").eq(index)
    var suc = $(".succ")
    if($("#input-pw2").val()==""){
        table.removeClass()
        table.addClass("err popb")
        $(".b-p")[index].innerHTML = "请再次输入密码"
        return;
    }else  if($("#input-pw2").val()!=$("#input-psd").val()){
        table.removeClass();
        table.addClass("warn popb")
        $(".b-p")[index].innerHTML = "两次密码不一致"
    } else {
        table.addClass("con");
        suc.eq(index).removeClass("con")
    }
    regisBtnUse();
};
var phoneConfirm = function () {
    var index = $("input").index($("#input-phone"));
    var table = $("table").eq(index)
    var suc = $(".succ")
    if($("#input-phone").val()==""){
        table.removeClass()
        table.addClass("err popb")
        $(".b-p")[index].innerHTML = "请输入手机号"
        return;
    }else {
        table.addClass("con");
        suc.eq(index).removeClass("con")
        $(".btn-getsms").eq(0).removeClass("btn-disabled");
    }
    regisBtnUse();
};
var smsConfrim = function () {
    var index = $("input").index($("#input-sms"));
    var table = $("table").eq(index)
    var suc = $(".succ")
    if($("#input-sms").val()==""){
        table.removeClass()
        table.addClass("err popb")
        $(".b-p")[index].innerHTML = "请输入验证码"
        return;
    }else {
        table.addClass("con");
        suc.eq(index).removeClass("con")
    }
    regisBtnUse();
};
/*注册按钮是否可用*/
var regisBtnUse = function () {
    var name = $("#input-account").val(),
        pwd = $("#input-psd").val(),
        pwd2 = $("#input-pw2").val(),
        phone = $("#input-phone").val(),
        sms = $("#input-sms").val();
    if(name&&pwd&&pwd2&&phone&&sms){
        console.log(1)
        $(".b-btn").eq(0).removeClass("btndisabled");
    }
};
var registerHandler = function () {
    var name = $("#input-account").val(),
        pwd = $("#input-psd").val(),
        pwd2 = $("#input-pw2").val(),
        phone = $("#input-phone").val(),
        sms = $("#input-sms").val();
};