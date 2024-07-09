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
    parser.add_argument("--algorithm-id", type=str, default='')
    parser.add_argument("--table-name", type=str, default='')
    parser.add_argument("--data-type", type=str, default='')
    parser.add_argument("--score-id", type=str, default='')
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
    parser.add_argument('--save-path', type=str, default='E:\\07_MedAIProject\\software8\\01_BackEnd\\SHEMiningV2.0\\src\\main\\resources\\results\\')

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