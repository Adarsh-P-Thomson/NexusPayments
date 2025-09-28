import plotly.graph_objects as go
import plotly.express as px
import numpy as np

# Define component positions and types
components = {
    "React Frontend": {"x": 5, "y": 9, "type": "frontend", "color": "#1FB8CD"},
    "API Gateway": {"x": 5, "y": 7.5, "type": "gateway", "color": "#DB4545"},
    "Identity Service": {"x": 2, "y": 6, "type": "service", "color": "#2E8B57"},
    "Billing Service": {"x": 4, "y": 6, "type": "service", "color": "#2E8B57"},
    "Metering Service": {"x": 6, "y": 6, "type": "service", "color": "#2E8B57"},
    "ML Service": {"x": 8, "y": 6, "type": "service", "color": "#2E8B57"},
    "PostgreSQL": {"x": 2, "y": 4, "type": "database", "color": "#5D878F"},
    "MongoDB": {"x": 6, "y": 4, "type": "database", "color": "#5D878F"},
    "Redis": {"x": 8, "y": 4, "type": "cache", "color": "#D2BA4C"},
    "Apache Kafka": {"x": 4, "y": 4, "type": "messaging", "color": "#B4413C"},
    "Stripe": {"x": 1, "y": 2, "type": "external", "color": "#964325"},
    "TaxJar": {"x": 3, "y": 2, "type": "external", "color": "#964325"},
    "Prometheus": {"x": 9, "y": 2, "type": "monitoring", "color": "#944454"},
    "Grafana": {"x": 9, "y": 3.5, "type": "monitoring", "color": "#944454"},
    "Jaeger": {"x": 7, "y": 2, "type": "monitoring", "color": "#944454"}
}

# Define connections
connections = [
    ("React Frontend", "API Gateway"),
    ("API Gateway", "Identity Service"),
    ("API Gateway", "Billing Service"),
    ("API Gateway", "Metering Service"),
    ("API Gateway", "ML Service"),
    ("Identity Service", "PostgreSQL"),
    ("Billing Service", "PostgreSQL"),
    ("Billing Service", "Apache Kafka"),
    ("Metering Service", "MongoDB"),
    ("Metering Service", "Apache Kafka"),
    ("Apache Kafka", "ML Service"),
    ("ML Service", "MongoDB"),
    ("Billing Service", "Stripe"),
    ("Billing Service", "TaxJar"),
    ("Prometheus", "Grafana")
]

# Create the figure
fig = go.Figure()

# Add connection lines
for start, end in connections:
    start_comp = components[start]
    end_comp = components[end]
    
    fig.add_trace(go.Scatter(
        x=[start_comp["x"], end_comp["x"]],
        y=[start_comp["y"], end_comp["y"]],
        mode='lines',
        line=dict(color='#13343B', width=2, dash='solid'),
        hoverinfo='none',
        showlegend=False
    ))

# Add component nodes with different shapes and colors by type
for name, comp in components.items():
    symbol = 'circle'
    if comp["type"] == "database":
        symbol = 'diamond'
    elif comp["type"] == "external":
        symbol = 'square'
    elif comp["type"] == "gateway":
        symbol = 'hexagon'
    elif comp["type"] == "messaging":
        symbol = 'pentagon'
    
    fig.add_trace(go.Scatter(
        x=[comp["x"]],
        y=[comp["y"]],
        mode='markers+text',
        marker=dict(
            size=30,
            color=comp["color"],
            symbol=symbol,
            line=dict(width=2, color='#13343B')
        ),
        text=name.replace(' ', '<br>'),
        textposition='middle center',
        textfont=dict(size=10, color='white'),
        name=comp["type"].title(),
        hovertemplate=f'<b>{name}</b><br>Type: {comp["type"]}<extra></extra>',
        showlegend=False if any(trace.name == comp["type"].title() for trace in fig.data) else True
    ))

# Add dotted lines for monitoring and caching connections
monitoring_connections = [
    ("Identity Service", "Prometheus"),
    ("Billing Service", "Prometheus"),
    ("Metering Service", "Prometheus"),
    ("ML Service", "Prometheus"),
    ("Identity Service", "Jaeger"),
    ("Billing Service", "Jaeger"),
    ("Metering Service", "Jaeger"),
    ("ML Service", "Jaeger"),
    ("Identity Service", "Redis"),
    ("Billing Service", "Redis"),
    ("Metering Service", "Redis"),
    ("ML Service", "Redis")
]

for start, end in monitoring_connections:
    start_comp = components[start]
    end_comp = components[end]
    
    fig.add_trace(go.Scatter(
        x=[start_comp["x"], end_comp["x"]],
        y=[start_comp["y"], end_comp["y"]],
        mode='lines',
        line=dict(color='#13343B', width=1, dash='dot'),
        hoverinfo='none',
        showlegend=False,
        opacity=0.5
    ))

# Update layout
fig.update_layout(
    title="NexusPay System Architecture",
    xaxis=dict(
        range=[0, 10],
        showgrid=False,
        showticklabels=False,
        zeroline=False
    ),
    yaxis=dict(
        range=[1, 10],
        showgrid=False,
        showticklabels=False,
        zeroline=False
    ),
    showlegend=True,
    legend=dict(
        orientation='h',
        yanchor='bottom',
        y=1.05,
        xanchor='center',
        x=0.5
    ),
    plot_bgcolor='rgba(0,0,0,0)',
    paper_bgcolor='rgba(0,0,0,0)'
)

# Remove axis
fig.update_xaxes(visible=False)
fig.update_yaxes(visible=False)

# Add layer labels as annotations
fig.add_annotation(x=0.5, y=9, text="Frontend", showarrow=False, font=dict(size=14, color="#13343B"), xref="x", yref="y")
fig.add_annotation(x=0.5, y=7.5, text="Gateway", showarrow=False, font=dict(size=14, color="#13343B"), xref="x", yref="y")
fig.add_annotation(x=0.5, y=6, text="Services", showarrow=False, font=dict(size=14, color="#13343B"), xref="x", yref="y")
fig.add_annotation(x=0.5, y=4, text="Data Layer", showarrow=False, font=dict(size=14, color="#13343B"), xref="x", yref="y")
fig.add_annotation(x=0.5, y=2, text="External", showarrow=False, font=dict(size=14, color="#13343B"), xref="x", yref="y")

# Save the chart
fig.write_image("nexuspay_architecture.png")
fig.write_image("nexuspay_architecture.svg", format="svg")

print("NexusPay architecture diagram created successfully!")
print("Saved as nexuspay_architecture.png and nexuspay_architecture.svg")