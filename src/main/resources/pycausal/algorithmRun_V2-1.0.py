# coding=utf-8
import psycopg2
import argparse
import numpy as np
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
import networkx as nx
import matplotlib.patheffects as path_effects
from mpl_toolkits.mplot3d.art3d import Line3DCollection
import pydot
import pandas as pd

from causallearn.utils.GraphUtils import GraphUtils
from dagma_linear import adjacency_matrix_to_pydot
from causallearn.search.ConstraintBased.PC import pc
from dagma_linear import DAGMA_linear
from causallearn.search.FCMBased import lingam
from notears import linear
from sklearn.preprocessing import StandardScaler


matplotlib.rc("font", family='SimHei')
plt.rcParams["font.sans-serif"] = ["Microsoft YaHei"]  # 设置字体
plt.rcParams["axes.unicode_minus"] = False  # 该语句解决图像中的“-”负号的乱码问题


def getMutiDigragh(pyd):
    node_mapping = {str(i+1): feature_names[i] for i in range(len(feature_names))}

    # Convert Pydot to NetworkX graph manually
    nx_graph = nx.DiGraph()
    for edge in pyd.get_edges():
        source_id = int(edge.get_source())  # 获取原始的节点编号（可能是从0开始）
        destination_id = int(edge.get_destination())
        # 确保源和目标节点编号在映射中，如果PC输出的节点编号从0开始，则无需 +1
        nx_graph.add_edge(node_mapping[str(source_id + 1)], node_mapping[str(destination_id + 1)])
    # Create DOT format string
    dot_string = "digraph g {\n"
    for edge in nx_graph.edges():
        dot_string += f'"{edge[0]}" -> "{edge[1]}"  [arrowhead=normal, arrowtail=none];\n'
    dot_string += "}"
    graphs = pydot.graph_from_dot_data(dot_string)
    print(graphs[0])

    H = nx.nx_pydot.from_pydot(graphs[0])
    return H


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--algorithm-id", type=str, default='notears')
    parser.add_argument("--table-name", type=str, default='lungcancer_dev')
    parser.add_argument("--data-type", type=str, default='discrete')
    parser.add_argument("--score-id", type=str, default='cg-bic-score')
    parser.add_argument("--max-degree", type=int, default=3)

    parser.add_argument("--province", type=str, default='')
    parser.add_argument("--city", type=str, default='')
    parser.add_argument("--county", type=str, default='')

    parser.add_argument("--clinical-representation", type=str,
                        default="FrequentCold,chronicLungDisease,ChestPain,Fatigue,Wheezing,OccuPationalHazards,DryCough,Gender,SwallowingDifficulty,Snoring,ShortnessofBreath,DustAllergy,CoughingofBlood,ClubbingofFingerNails,")
    parser.add_argument("--living-habit", type=str,
                        default="Obesity,AlcoholUse,WeightLoss,AirPollution,GeneticRisk,BalancedDiet,")
    parser.add_argument("--social-connection", type=str, default="PassiveSmoker,Smoking,")

    parser.add_argument('--database-url', type=str, default='10.16.48.219')
    parser.add_argument('--database-password', type=str, default='111111')
    parser.add_argument('--database-user', type=str, default='pg')
    parser.add_argument('--save-path', type=str, default='E:\\07_MedAIProject\\software8\\01_BackEnd\\SHEMiningV2.0\\src\\main\\resources\\test\\')

    args = parser.parse_args()

    # 数据库连接
    url = args.database_url
    password = args.database_password
    user = args.database_user
    # 算法参数
    algorithmId = args.algorithm_id
    print(algorithmId)
    tableName = args.table_name

    if args.data_type == "离散数据":
        dataType = "discrete"
    else:
        dataType = "continuous"
    scoreId = args.score_id
    maxDegree = args.max_degree
    # 地区
    province = args.province
    city = args.city
    county = args.county
    # 通过切分得到每一个危险因素
    ClinicalRepresentationStr = args.clinical_representation[0:-1].split(',')
    LivingHabitStr = args.living_habit[0:-1].split(',')
    SocialConnectionStr = args.social_connection[0:-1].split(',')

    ClinicalRepresentation = args.clinical_representation.split(',')
    LivingHabit = args.living_habit.split(',')
    SocialConnection = args.social_connection.split(',')

    # 图片保存路径
    savePath = args.save_path
    tailName = ".png"
    savePathcls = savePath + tableName + 'cls' + tailName
    savePathc = savePath + tableName + 'c' + tailName
    savePathl = savePath + tableName + 'l' + tailName
    savePaths = savePath + tableName + 's' + tailName
    savePathcl = savePath + tableName + 'cl' + tailName
    savePathls = savePath + tableName + 'ls' + tailName

    conn = psycopg2.connect(
        dbname="software8",
        user="pg",
        password="111111",
        host="10.16.48.219",
        port="5432"
    )

    factorStr = ""
    for item in ClinicalRepresentationStr:
        factorStr += "\"" + item + "\","
    for item in LivingHabitStr:
        factorStr += "\"" + item + "\","
    for item in SocialConnectionStr:
        factorStr += "\"" + item + "\","

    factorStr = factorStr[0:-1]

    if province == '':
        selectStr = "select " + factorStr + \
                    " from " + tableName + ";"

    elif city == '':
        selectStr = "select " + factorStr + \
                    " from " + tableName + \
                    " where province=\'" + province + "\';"
    elif county == '':
        selectStr = "select " + factorStr + \
                    " from " + tableName + \
                    " where province=\'" + province + "\' and " + "city= \'" + city + "\';"
    else:
        selectStr = "select " + factorStr + \
                    " from " + tableName + \
                    " where province=\'" + province + "\' and " + "city= \'" + city + "\' and " + " county=\'" + county + "\';"

    df = pd.read_sql(selectStr, con=conn)
    feature_names = list(df.columns)
    print(feature_names)
    conn.commit()
    conn.close()
    df = df.apply(pd.to_numeric, errors='coerce')

    df = df.dropna()  # Example of dropping rows with NaN values



    # 执行pc
    if algorithmId == 'pc':

        cg = pc(df.values, node_names=feature_names)
        print(cg.G.get_graph_edges())

        # visualization using pydot
        cg.draw_pydot_graph()

        # or save the graph
        pyd = GraphUtils.to_pydot(cg.G)

        H = getMutiDigragh(pyd=pyd)

    if algorithmId == 'dagma_linear':

        model = DAGMA_linear(loss_type='l2')
        W_est = model.fit(df.values, lambda1=0.02)
        pyd= adjacency_matrix_to_pydot(W_est, feature_names)
        H = getMutiDigragh(pyd=pyd)


    if algorithmId == 'lingam':


        scaler = StandardScaler()
        scaled_data = scaler.fit_transform(df.values)
        # 初始化并应用ICALiNGAM模型
        model = lingam.ICALiNGAM(1, 1000)
        model.fit(scaled_data)
        # 将邻接矩阵转换为图结构，并使用特征名称标记节点
        pyd = adjacency_matrix_to_pydot(model.adjacency_matrix_, feature_names)

        H = getMutiDigragh(pyd=pyd)


    if algorithmId == 'd-lingam':


        scaler = StandardScaler()
        scaled_data = scaler.fit_transform(df.values)
        # 初始化并应用ICALiNGAM模型
        model = lingam.DirectLiNGAM()
        model.fit(scaled_data)
        # 将邻接矩阵转换为图结构，并使用特征名称标记节点
        pyd = adjacency_matrix_to_pydot(model.adjacency_matrix_, feature_names)

        H = getMutiDigragh(pyd=pyd)


    if algorithmId == 'notears':

        from notears import dag_to_graph

        W_est = linear(df.values, lambda1=0.1, loss_type='l2')  # 这是估计的权重矩阵
        pyd = adjacency_matrix_to_pydot(W_est, feature_names)
        H = getMutiDigragh(pyd=pyd)


    ClinicalRepresentation_nodes = []
    for i in range(len(ClinicalRepresentation)):
        ClinicalRepresentation_nodes.append((i, {"factor": ClinicalRepresentation[i]}))

    # LivingHabit = ["AlcoholUse", "BalancedDiet", "Obesity", "Smoking", "PassiveSmoker"]
    LivingHabit_nodes = []
    for i in range(len(LivingHabit)):
        LivingHabit_nodes.append((i, {"factor": LivingHabit[i]}))

    # SocialConnection = ["AirPollution", "OccuPationalHazards", "ExposuretoEnvironmentalRiskFactors"]
    SocialConnection_nodes = []
    for i in range(len(SocialConnection)):
        SocialConnection_nodes.append((i, {"factor": SocialConnection[i]}))

    line_between_layers = {}
    line_01 = []
    line_12 = []
    line_20 = []
    G1 = nx.DiGraph.subgraph(H, ClinicalRepresentation)
    for edge in list(H.edges):
        if edge[0] in ClinicalRepresentation and edge[1] in LivingHabit:
            line_01.append((ClinicalRepresentation.index(edge[0]), LivingHabit.index(edge[1])))
        if edge[0] in LivingHabit and edge[1] in SocialConnection:
            line_12.append((LivingHabit.index(edge[0]), SocialConnection.index(edge[1])))
        if edge[0] in SocialConnection and edge[1] in ClinicalRepresentation:
            line_20.append((SocialConnection.index(edge[0]), ClinicalRepresentation.index(edge[1])))
    line_between_layers.update({0: line_01})
    line_between_layers.update({1: line_12})
    line_between_layers.update({2: line_20})

    G1_edges = list(G1.edges)
    G11_edges = []
    first = 0
    second = 0
    for edge in G1_edges:
        if edge[0] in ClinicalRepresentation:
            first = ClinicalRepresentation.index(edge[0])
        if edge[1] in ClinicalRepresentation:
            second = ClinicalRepresentation.index(edge[1])
        G11_edges.append((first, second))

    G2 = nx.DiGraph.subgraph(H, LivingHabit)

    G2_edges = list(G2.edges)
    G21_edges = []
    for edge in G2_edges:
        if edge[0] in LivingHabit:
            first = LivingHabit.index(edge[0])
        if edge[1] in LivingHabit:
            second = LivingHabit.index(edge[1])
        G21_edges.append((first, second))

    G3 = nx.DiGraph.subgraph(H, SocialConnection)
    G3_edges = list(G3.edges)
    G31_edges = []
    for edge in G3_edges:
        if edge[0] in SocialConnection:
            first = SocialConnection.index(edge[0])
        if edge[1] in SocialConnection:
            second = SocialConnection.index(edge[1])
        G31_edges.append((first, second))

    cols = ['steelblue', 'darksalmon', 'mediumseagreen']

    # G11 = nx.Graph(name="Clinical Representation")
    G11 = nx.Graph(name="临床表征")
    G11.add_nodes_from(ClinicalRepresentation_nodes)
    G11.add_edges_from(G11_edges)
    G11.pos = nx.circular_layout(G11)
    nx.draw_networkx(G11, with_labels=True)

    # G21 = nx.Graph(name="Living Habit")
    G21 = nx.Graph(name="生活行为习惯")
    G21.add_nodes_from(LivingHabit_nodes)
    G21.add_edges_from(G21_edges)
    G21.pos = nx.circular_layout(G21)

    # G31 = nx.Graph(name="Social Environment")
    G31 = nx.Graph(name="社会环境")
    G31.add_nodes_from(SocialConnection_nodes)
    G31.add_edges_from(G31_edges)
    G31.pos = nx.circular_layout(G31)

    graphs = [G11, G21, G31]
    # 三个层次
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes

        cs = [cols[gi]] * len(xs)

        lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
        line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
        ax.add_collection3d(line_collection)

        ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)

        GDict = dict(G.nodes).values()
        for li, lab in enumerate(GDict):
            ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                    ha='center', va='center')

        xdiff = max(xs) - min(xs)
        ydiff = max(ys) - min(ys)
        ymin = min(ys) - ydiff * 0.1
        ymax = max(ys) + ydiff * 0.1
        xmin = min(xs) - xdiff * 0.1 * (w / h)
        xmax = max(xs) + xdiff * 0.1 * (w / h)
        xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
        zz = np.zeros(xx.shape) + gi
        ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)

        layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                            color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                            path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                          path_effects.Normal()])

    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.5, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    ax.dist = 11

    ax.set_axis_off()

    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/file/results/cls.png', dpi=425, bbox_inches='tight')
    plt.savefig(savePathcls, dpi=425, bbox_inches='tight')

    # 两个层次
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi != 2:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 11.0

    ax.set_axis_off()

    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/file/results/cl.png', dpi=425, bbox_inches='tight')
    plt.savefig(savePathcl, dpi=425, bbox_inches='tight')

    # plt.savefig('multilayer_network_wlabels.png',dpi=425,bbox_inches='tight')

    # 没有第1层
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi != 0:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 10.0

    ax.set_axis_off()
    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/file/results/ls.png', dpi=425, bbox_inches='tight')
    plt.savefig(savePathls, dpi=425, bbox_inches='tight')

    # plt.savefig('multilayer_network_wlabels.png',dpi=425,bbox_inches='tight')

    # 一个层次
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi == 2:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 11.0

    ax.set_axis_off()

    plt.savefig(savePaths, dpi=425, bbox_inches='tight')
    # plt.savefig('multilayer_network_wlabels.png',dpi=425,bbox_inches='tight')

    # 第二层
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi == 1:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 11.0

    ax.set_axis_off()
    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/results/l.png', dpi=425, bbox_inches='tight')

    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/file/results/l.png', dpi=425, bbox_inches='tight')
    plt.savefig(savePathl, dpi=425, bbox_inches='tight')
    # plt.savefig('multilayer_network_wlabels.png',dpi=425,bbox_inches='tight')

    # 第三层
    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi == 0:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 11.0

    ax.set_axis_off()
    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/results/c.png', dpi=425, bbox_inches='tight')
    # plt.savefig('H:/01_Future/code/MedAI/SHEMining/src/main/resources/file/results/c.png', dpi=425, bbox_inches='tight')
    plt.savefig(savePathc, dpi=425, bbox_inches='tight')
    # plt.savefig('multilayer_network_wlabels.png',dpi=425,bbox_inches='tight')

    w = 10
    h = 8

    fig, ax = plt.subplots(1, 1, figsize=(w, h), dpi=200, subplot_kw={'projection': '3d'})

    for gi, G in enumerate(graphs):
        # 节点位置
        xs = list(list(zip(*list(G.pos.values())))[0])
        ys = list(list(zip(*list(G.pos.values())))[1])
        zs = [gi] * len(xs)  # set a common z-position of the nodes
        # 节点颜色node colors
        cs = [cols[gi]] * len(xs)

        if gi == 2:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.8)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7,
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.3, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.95', fontsize=15, zorder=1e5, ha='left', va='center',
                                path_effects=[path_effects.Stroke(linewidth=2, foreground=cols[gi]),
                                              path_effects.Normal()])
        else:
            # 添加层内连线
            lines3d = [(list(G.pos[i]) + [gi], list(G.pos[j]) + [gi]) for i, j in G.edges()]
            line_collection = Line3DCollection(lines3d, zorder=gi - 1, color=cols[gi], alpha=0.1)
            ax.add_collection3d(line_collection)

            # 添加节点
            ax.scatter(xs, ys, zs, c=cs, s=125, marker='o', alpha=0.1, zorder=gi + 1)
            # 添加节点标签
            GDict = dict(G.nodes).values()
            for li, lab in enumerate(GDict):
                ax.text(xs[li], ys[li], zs[li] + 0.11, lab.get('factor'), zorder=gi + 200, fontsize=7, color='0.9',
                        ha='center', va='center')

            # 添加层平面
            xdiff = max(xs) - min(xs)
            ydiff = max(ys) - min(ys)
            ymin = min(ys) - ydiff * 0.1
            ymax = max(ys) + ydiff * 0.1
            xmin = min(xs) - xdiff * 0.1 * (w / h)
            xmax = max(xs) + xdiff * 0.1 * (w / h)
            xx, yy = np.meshgrid([xmin, xmax], [ymin, ymax])
            zz = np.zeros(xx.shape) + gi
            ax.plot_surface(xx, yy, zz, color=cols[gi], alpha=0.1, zorder=gi)
            # 添加平面标签
            layertext = ax.text(1.2, -1.2, gi * 0.95 + 0.5, G.name,
                                color='.9', fontsize=15, zorder=1e5, ha='left', va='center')

    # 层间连线
    for gi in range(3):
        if gi != 2:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi + 1].pos[i[1]]) + [gi + 1]) for i in
                               line_between_layers[gi]]
        else:
            lines3d_between = [(list(graphs[gi].pos[i[0]]) + [gi], list(graphs[gi - 2].pos[i[1]]) + [gi - 2]) for i in
                               line_between_layers[gi]]
        between_lines = Line3DCollection(lines3d_between, zorder=gi, color='.3',
                                         alpha=0.1, linestyle='--', linewidth=1)
        ax.add_collection3d(between_lines)

    # 把图放在坐标系上
    ax.set_ylim(min(ys) - ydiff * 0.1, max(ys) + ydiff * 0.1)
    ax.set_xlim(min(xs) - xdiff * 0.1, max(xs) + xdiff * 0.1)
    ax.set_zlim(-0.1, len(graphs) - 1 + 0.1)

    # 选择显示视角
    angle = 30
    height_angle = 12
    ax.view_init(height_angle, angle)

    # 选择显示距离
    ax.dist = 11.0

    ax.set_axis_off()


