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