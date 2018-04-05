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
    var myTree = null;
    var myNode = null;
    var myLabel = null;
    var myChart = null;
    var myTable = null;

    buildPage("40000");

    function buildPage(id) {
        buildTree(id);
        buildTable(id);
        buildChart(id);
    }


    function buildTree(id) {
        $.getJSON("/app/api/dsb/organizationTree/00000", function (data) {
            var myData = [data];
            myTree = $('#tree').tree({
                primaryKey: 'id',
                dataSource: myData,
                icons: {
                    expand: '<i>+</i>',
                    collapse: '<i>-</i>'
                }
            });
            myTree.expand(myTree.getNodeById("00000"));
            myNode = myTree.getNodeById(id);
            myTree.select(myNode);
            myLabel = myNode.find('span[data-role~="display"]').html();

            myTree.off().on('select', function (e, node, newid) {
                if (myChart != null) {
                    myChart.destroy();
                }
                updatePage(newid);
            });
        });
    }

    function buildTable(id) {
        myTable = $('#playerTable').DataTable({
            "lengthMenu": [[100, 500, 1000, -1], [100, 500, 1000, "All"]],
            "ajax": {
                "url": "/app/api/dsb/players/" + id,
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

    function buildChart(id) {
        document.getElementById("chessCharts");
        $.ajax({
            url: '/app/api/dsb/stats/' + id,
            dataType: 'json'
        }).done(function (results) {
            var labels = [], dwzByAge = [], dwzDSBByAge = [], eloByAge = [], eloDSBByAge = [], members = [];
            for (var i = 0; i < results.length; i++) {
                labels.push(results[i].age);
                dwzByAge.push(results[i].dwz);
                dwzDSBByAge.push(results[i].dwzDSB);
                eloByAge.push(results[i].elo);
                eloDSBByAge.push(results[i].eloDSB);
                members.push(results[i].members)
            }
            updateChart(labels, dwzByAge, dwzDSBByAge, eloByAge, eloDSBByAge, members);
        });
    }

    function updatePage(id) {
        myNode = myTree.getNodeById(id);
        myLabel = myNode.find('span[data-role~="display"]').html();

        if ($.fn.dataTable.isDataTable('#playerTable')) {
            myTable = $('#playerTable').DataTable();
            myTable.ajax.url("/app/api/dsb/players/" + id).load();
        }

        buildChart(id);
    }

    function updateChart(labels, dwzByAge, dwzDSBByAge, eloByAge, eloDSBByAge, members) {
        myChart = new Chart(document.getElementById("chessCharts"), {
            type: 'line',
            options: {
                title: {
                    display: true,
                    text: "Chess Statistics for " + myLabel
                },
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Age'
                        }
                    }],
                    yAxes: [{
                        id: 'ratings',
                        position: 'left',
                        scaleLabel: {
                            display: true,
                            labelString: 'DWZ/ELO Ratings'
                        }
                    }, {
                        id: 'members',
                        position: 'right',
                        scaleLabel: {
                            display: true,
                            labelString: '# of Members'
                        }
                    }]
                },
                legend: {
                    display: true
                }
            },
            data: {
                labels: labels,
                datasets: [{
                    data: dwzByAge,
                    lineTension: 0,
                    label: 'Avg. DWZ by Age (' + myLabel + ")",
                    yAxisID: 'ratings',
                    backgroundColor: '#0000FF',
                    borderColor: '#0000FF',
                    borderWidth: 1,
                    pointBackgroundColor: '#0000FF',
                    fill: false
                }, {
                    data: dwzDSBByAge,
                    lineTension: 0,
                    label: "Avg. DWZ by Age (00000: Deutscher Schachbund)",
                    yAxisID: "ratings",
                    backgroundColor: '#00FFFF',
                    borderColor: '#00FFFF',
                    borderWidth: 1,
                    pointBackgroundColor: '#00FFFF',
                    fill: false
                }, {
                    data: eloByAge,
                    lineTension: 0,
                    label: 'Avg. ELO by Age (' + myLabel + ")",
                    yAxisID: 'ratings',
                    backgroundColor: '#FF0000',
                    borderColor: '#FF0000',
                    borderWidth: 1,
                    pointBackgroundColor: '#FF0000',
                    fill: false
                }, {
                    data: eloDSBByAge,
                    lineTension: 0,
                    label: 'Avg. ELO by Age (00000: Deutscher Schachbund)',
                    type: 'line',
                    yAxisID: 'ratings',
                    backgroundColor: '#FF8040',
                    borderColor: '#FF8040',
                    borderWidth: 1,
                    pointBackgroundColor: '#FF8040',
                    fill: false
                }, {
                    data: members,
                    lineTension: 0,
                    label: 'Members (' + myLabel + ")",
                    type: 'line',
                    yAxisID: 'members',
                    backgroundColor: '#008000',
                    borderColor: '#008000',
                    borderWidth: 1,
                    pointBackgroundColor: '#008000'
                }]
            }
        });
    }
});





