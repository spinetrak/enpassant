/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 spinetrak
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

$(document).ready(function () {
    buildPage("40000");
});

var buildPage = function (id) {

    var myLabel = "";

    $.getJSON("/app/api/dsb/associationTree/00000", function (data) {
        var myData = [data];
        var tree = $('#tree').tree({
            primaryKey: 'id',
            dataSource: myData,
            icons: {
                expand: '<i>+</i>',
                collapse: '<i>-</i>'
            }
        });
        tree.expand(tree.getNodeById("00000"));
        var myNode = tree.getNodeById(id);
        myLabel = myNode.find('span[data-role~="display"]').html();
        tree.select(myNode);

        tree.on('select', function (e, node, id) {
            buildPage(id);
        });
    });


    if ($.fn.dataTable.isDataTable('#example')) {
        var table = $('#example').DataTable();
        table.ajax.url("/app/api/dsb/association/" + id).load();
    }
    else {
        var table = $('#example').DataTable({
            "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
            "ajax": {
                "url": "/app/api/dsb/association/" + id,
                "dataSrc": "players"
            },
            "columns": [
                {
                    data: null, render: function (data, type, row) {
                    return data.clubId + '-' + data.memberId;
                }
                },
                {
                    data: "dsbId", "render": function (data, type, row) {
                    return data === 0 ? '' : data;
                }
                },
                {
                    "data": "fideId", "render": function (data, type, row) {
                    return data === 0 ? '' : data;
                }
                },
                {"data": "name"},
                {"data": "gender"},
                {"data": "yoB"},
                {
                    "data": "currentDWZ", "render": function (data, type, row) {
                    return data === 0 ? '' : data;
                }
                },
                {
                    "data": "currentELO", "render": function (data, type, row) {
                    return data === 0 ? '' : data;
                }
                },
                {"data": "status"},
                {"data": "eligibility"}
            ]
        });
    }

    var ctx = document.getElementById("myChart");
    var jsonData = $.ajax({
        url: '/app/api/dsb/stats/' + id,
        dataType: 'json'
    }).done(function (results) {
        var labels = [], dwzByAge = [], dwzDSBByAge = [], eloByAge = [], eloDSBByAge = [];
        for (var i = 0; i < results.length; i++) {
            var stats = results[i];
            labels.push(stats.age);
            dwzByAge.push(stats.dwz);
            dwzDSBByAge.push(stats.dwzDSB);
            eloByAge.push(stats.elo);
            eloDSBByAge.push(stats.eloDSB);
        }

        var myChart = new Chart(ctx, {
            type: 'line',
            options: {
                title: {
                    display: true,
                    text: "Chess Statistics for " + myLabel
                }
            },
            data: {
                labels: labels,
                datasets: [{
                    data: dwzByAge,
                    lineTension: 0,
                    label: "Avg. DWZ by Age",
                    backgroundColor: 'transparent',
                    borderColor: '#637fed',
                    borderWidth: 1,
                    pointBackgroundColor: '#637fed'
                }, {
                    data: dwzDSBByAge,
                    lineTension: 0,
                    label: "Avg. DSB DWZ by Age",
                    backgroundColor: 'transparent',
                    borderColor: '#09279b',
                    borderWidth: 1,
                    pointBackgroundColor: '#09279b'
                }, {
                    data: eloByAge,
                    lineTension: 0,
                    label: "Avg. ELO by Age",
                    backgroundColor: 'transparent',
                    borderColor: '#f44250',
                    borderWidth: 1,
                    pointBackgroundColor: '#f44250'
                }, {
                    data: eloDSBByAge,
                    lineTension: 0,
                    label: "Avg. DSB ELO by Age",
                    backgroundColor: 'transparent',
                    borderColor: '#63040c',
                    borderWidth: 1,
                    pointBackgroundColor: '#63040c'
                }]
            }
        })
    });
};



