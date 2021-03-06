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
    var myLabel = "40000: Hamburger Schachverband";
    var myChart = null;
    var myTable = null;
    var myID = "40000";

    buildPage(myID);

    function buildPage(id) {
        buildFooter();
        buildTree(id);
        buildTable(id);
        buildChart(id);
    }

    function buildFooter() {
        var e = document.getElementById("buildInfo");
        $.ajax({
            url: '/api/git/buildInfo',
            dataType: 'text'
        }).done(function (results) {
            e.innerHTML = results;
        });
    }

    function buildTree(id) {
        $.getJSON("/api/dsb/organizationTree/00000", function (data) {
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
                if (myTable != null) {
                    myTable.clear().draw();
                }
                myID = newid;
                updatePage(myID);
            });
        });
    }

    function buildTable(id) {

        $('#playerTable thead tr').clone(true).appendTo('#playerTable thead');
        $('#playerTable thead tr:eq(1) th').each(function (i) {
            var title = $(this).text();
            $(this).html('<input type="text" placeholder="Search ' + title + '" />');

            $('input', this).on('keyup change', function () {
                if (myTable.column(i).search() !== this.value) {
                    myTable
                        .column(i)
                        .search(this.value)
                        .draw();
                }
            });
        });

        myTable = $('#playerTable').DataTable({
            "lengthMenu": [[100, 500, 1000, -1], [100, 500, 1000, "All"]],
            "ajax": {
                "url": "/api/dsb/players/" + id,
                "dataSrc": ""
            },
            "columns": [
                {
                    data: null, render: function (data, type, row) {
                    return "<a href='#'>" + data.clubId + '-' + data.memberId + "</a>";
                    }
                },
                {
                    data: "dsbId", "render": function (data, type, row) {
                    return data === 0 ? '' : "<a target='_blank' href='https://www.schachbund.de/spieler/" + data + ".html'>" + data + "</a>";
                    }
                },
                {
                    data: "fideId", render: function (data, type, row) {
                    return data === 0 ? '' : "<a target='_blank' href='https://ratings.fide.com/card.phtml?event=" + data + "'>" + data + "</a>";
                    }
                },
                {
                    data: "name", render: function (data, type, row) {
                    return data == 0 ? '' : "<a href='#'>" + data + "</a>";
                    }
                },
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
            ],
            "order": [[ 6, "desc" ]]
        });

        myTable.off('click').on('click', 'thead tr', function () {
            if (myChart != null) {
                myChart.destroy();
            }
            buildChart(myID);
        });

        myTable.on('click', 'tbody tr', function () {
            if (myChart != null) {
                myChart.destroy();
            }
            var arr = $('#playerTable').DataTable().row(this).data();
            if (typeof arr != 'undefined') {
                var clubId = arr.clubId;
                var memberId = arr.memberId;
                var player = clubId + '-' + memberId;
                myLabel = arr.name;
                showRatingsHistory(player);
            }
        });
    }

    function buildChart(id) {
        document.getElementById("chessCharts");
        $.ajax({
            url: '/api/dsb/stats/' + id,
            dataType: 'json'
        }).done(function (results) {
            var labels = [], dwzByAge = [], dwzDSBByAge = [], eloByAge = [], eloDSBByAge = [], members = [], membersWithDWZ = [], membersWithELO = [];
            for (var i = 0; i < results.length; i++) {
                labels.push(results[i].age);
                dwzByAge.push(results[i].dwz);
                dwzDSBByAge.push(results[i].dwzDSB);
                eloByAge.push(results[i].elo);
                eloDSBByAge.push(results[i].eloDSB);
                members.push(results[i].members);
                membersWithDWZ.push(results[i].membersWithDWZ);
                membersWithELO.push(results[i].membersWithELO);
            }
            updateChart(labels, dwzByAge, dwzDSBByAge, eloByAge, eloDSBByAge, members, membersWithDWZ, membersWithELO);
        });
    }

    function updatePage(id) {
        myNode = myTree.getNodeById(id);
        myLabel = myNode.find('span[data-role~="display"]').html();

        if ($.fn.dataTable.isDataTable('#playerTable')) {
            $('#playerTable').DataTable().ajax.url("/api/dsb/players/" + id).load();
        }

        buildChart(id);
    }

    function updateChart(labels, dwzByAge, dwzDSBByAge, eloByAge, eloDSBByAge, members, membersWithDWZ, membersWithELO) {
        Chart.defaults.global.defaultFontSize = 10;
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
                    display: true,
                    position: 'left'
                }
            },
            data: {
                labels: labels,
                datasets: [{
                    data: dwzByAge,
                    lineTension: 0,
                    label: 'Avg. DWZ by Age (' + myLabel + ')',
                    yAxisID: 'ratings',
                    backgroundColor: '#0FA211',
                    borderColor: '#0FA211',
                    borderWidth: 1,
                    pointRadius: 2,
                    fill: false
                }, {
                    data: dwzDSBByAge,
                    lineTension: 0,
                    label: "Avg. DWZ by Age (00000: Deutscher Schachbund)",
                    yAxisID: "ratings",
                    backgroundColor: '#0FA0A2',
                    borderColor: '#0FA0A2',
                    borderWidth: 1,
                    pointRadius: 2,
                    borderDashOffset: 3,
                    fill: false
                }, {
                    data: eloByAge,
                    lineTension: 0,
                    label: 'Avg. ELO by Age (' + myLabel + ')',
                    yAxisID: 'ratings',
                    backgroundColor: '#110FA2',
                    borderColor: '#110FA2',
                    borderWidth: 1,
                    pointRadius: 2,
                    fill: false
                }, {
                    data: eloDSBByAge,
                    lineTension: 0,
                    label: 'Avg. ELO by Age (00000: Deutscher Schachbund)',
                    type: 'line',
                    yAxisID: 'ratings',
                    backgroundColor: '#A20FA0',
                    borderColor: '#A20FA0',
                    borderWidth: 1,
                    pointRadius: 2,
                    fill: false
                }, {
                    data: membersWithELO,
                    lineTension: 0,
                    label: 'Members with ELO (' + myLabel + ')',
                    type: 'line',
                    yAxisID: 'members',
                    backgroundColor: '#A0A20F',
                    borderColor: '#A0A20F',
                    borderWidth: 1,
                    pointRadius: 0
                }, {
                    data: membersWithDWZ,
                    lineTension: 0,
                    label: 'Members with DWZ (' + myLabel + ")",
                    type: 'line',
                    yAxisID: 'members',
                    backgroundColor: '#A25B0F',
                    borderColor: '#A25B0F',
                    borderWidth: 1,
                    pointRadius: 0
                }, {
                    data: members,
                    lineTension: 0,
                    label: 'Members (' + myLabel + ')',
                    type: 'line',
                    yAxisID: 'members',
                    backgroundColor: '#A2110F',
                    borderColor: '#A2110F',
                    borderWidth: 1,
                    pointRadius: 0
                }]
            }
        });
    }

    function showRatingsHistory(id) {
        document.getElementById("chessCharts");
        $.ajax({
            url: '/api/dsb/playerStats/' + id,
            dataType: 'json'
        }).done(function (results) {
            var labels = [], eloHistory = [], dwzHistory = [];
            for (var i = 0; i < results.length; i++) {
                labels.push(results[i].lastEval);
                dwzHistory.push(results[i].dwz);
                eloHistory.push(results[i].elo);
            }
            updateRatingsChart(labels, dwzHistory, eloHistory);
        });


        function updateRatingsChart(labels, dwzHistory, eloHistory) {
            myChart = new Chart(document.getElementById("chessCharts"), {
                type: 'line',
                options: {
                    title: {
                        display: true,
                        text: "Chess Ratings for " + myLabel
                    },
                    scales: {
                        xAxes: [{
                            type: 'time',
                            display: true,
                            scaleLabel: {
                                display: true,
                                labelString: 'Time'
                            },
                            time: {
                                unit: 'month'
                            }
                        }],
                        yAxes: [{
                            id: 'ratings',
                            position: 'left',
                            scaleLabel: {
                                display: true,
                                labelString: 'DWZ/ELO Ratings'
                            }
                        }]
                    },
                    legend: {
                        display: true,
                        position: 'left'
                    }
                },
                data: {
                    labels: labels,
                    datasets: [{
                        data: dwzHistory,
                        lineTension: 0,
                        label: 'DWZ History (' + myLabel + ')',
                        yAxisID: 'ratings',
                        backgroundColor: '#0FA211',
                        borderColor: '#0FA211',
                        borderWidth: 2,
                        pointRadius: 1,
                        fill: false
                    }, {
                        data: eloHistory,
                        lineTension: 0,
                        label: 'ELO History (' + myLabel + ')',
                        yAxisID: "ratings",
                        backgroundColor: '#0FA0A2',
                        borderColor: '#0FA0A2',
                        borderWidth: 2,
                        pointRadius: 1,
                        borderDashOffset: 3,
                        fill: false
                    }]
                }
            });
        }

    }
});





