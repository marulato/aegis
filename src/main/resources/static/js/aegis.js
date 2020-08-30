var aegis = {};

aegis.showErrors = function(errorData) {
    aegis.clearErrors();
    $.each(errorData, function (index, element) {
       var input = $("input[name=" + index + "]")[0];
       var appendDiv = $("div[name=" + index + "_append]")[0];
       if (input == null || input == {} || input == '') {
           input = $("select[name=" + index + "]")[0];
       }
       $(input).addClass("is-invalid");
       var divName = "errorArea_" + index;
       if (appendDiv != null) {
           $(appendDiv).after("<div class='invalid-feedback' name='"+ divName +"'>" + element + "</div>");
       } else {
           $(input).after("<div class='invalid-feedback' name='"+ divName +"'>" + element + "</div>");
       }
    });
}

aegis.clearErrors = function () {
    $("div[name^=errorArea_]").each(function (idx, ele) {
        $(ele).remove();
    });
    $("#mainForm").find("input").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
    $("#mainForm").find("select").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
    $(".modal-body").find("input").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
    $(".modal-body").find("select").each(function (idx, ele) {
        $(ele).removeClass("is-invalid");
    });
}

aegis.required = function () {
    $("#mainForm").find("label").each(function (idx, ele) {
        if ($(ele).attr("data-required") == 'true') {
            var text = $(ele).html();
            text += "<span style='color: red'>&nbsp;*</span>";
            $(ele).html(text);
        }
    });
}

aegis.activate = function () {
    $("a[id^='link_']").each(function (idx, a) {
        $(a).click(function () {
            $("a[id^='link_']").each(function (i, link) {
                $(link).removeClass("active");
            });
            $(a).addClass("active");
        });
    });
}

aegis.loading = function() {
    $.blockUI({message:'<span style="font-size: 12px"><img src="/dist/img/loading.gif">&nbsp;正在处理，请稍后</span>'});
}

aegis.locale = {
    "sEmptyTable": "没有数据",
    "sInfo": "显示第 _START_ 至第 _END_ 项结果，共 _TOTAL_ 项",
    "sInfoEmpty": "显示第 0 至第 0 项结果，共 0 项",
    "sLengthMenu": "显示 _MENU_ 项结果",
    "oPaginate": {
        "sFirst": "首页",
        "sPrevious": "上一页",
        "sNext": "下一页",
        "sLast": "末页"
    }
}

aegis.toastrOptions = { // toastr配置
    "closeButton": true, //是否显示关闭按钮
    "debug": false, //是否使用debug模式
    "progressBar": true, //是否显示进度条，当为false时候不显示；当为true时候，显示进度条，当进度条缩短到0时候，消息通知弹窗消失
    "positionClass": "toast-top-right",//显示的动画时间
    "showDuration": "400", //显示的动画时间
    "hideDuration": "1000", //消失的动画时间
    "timeOut": "3000", //展现时间
    "extendedTimeOut": "1000", //加长展示时间
    "showEasing": "swing", //显示时的动画缓冲方式
    "hideEasing": "linear", //消失时的动画缓冲方式
    "showMethod": "fadeIn", //显示时的动画方式
    "hideMethod": "fadeOut" //消失时的动画方式
}
