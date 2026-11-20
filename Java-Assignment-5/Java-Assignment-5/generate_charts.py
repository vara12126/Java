import os
import matplotlib.pyplot as plt
import numpy as np

# Set style
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')
fig_dpi = 300

evidence_dir = "Java-Assignment-5/evidence/performance-charts"
os.makedirs(evidence_dir, exist_ok=True)

# --------------------------------------------------------------------------------------------------
# CHART 1: Buffering Comparison (Unbuffered vs Buffered)
# --------------------------------------------------------------------------------------------------
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5.5), dpi=fig_dpi)

sizes = ['1 MB', '10 MB', '50 MB']
unbuffered_ms = [4507, 29946, 151731]
buffered_stream_ms = [22, 132, 630]
buffered_reader_ms = [34, 60, 127]

x = np.arange(len(sizes))
width = 0.25

rects1 = ax1.bar(x - width, unbuffered_ms, width, label='Unbuffered FileInputStream', color='#e74c3c')
rects2 = ax1.bar(x, buffered_stream_ms, width, label='BufferedInputStream (8KB)', color='#3498db')
rects3 = ax1.bar(x + width, buffered_reader_ms, width, label='BufferedReader (readLine)', color='#2ecc71')

ax1.set_ylabel('Execution Time (ms) - Log Scale', fontsize=11, fontweight='bold')
ax1.set_title('I/O Line Counting Execution Time (Log Scale)', fontsize=12, fontweight='bold')
ax1.set_xticks(x)
ax1.set_xticklabels(sizes, fontweight='bold')
ax1.set_yscale('log')
ax1.legend(frameon=True)
ax1.grid(True, which="both", ls="--", alpha=0.5)

# Speedup chart
speedups = [4507/22, 29946/132, 151731/630]
bars = ax2.bar(sizes, speedups, color='#8e44ad', width=0.45)
ax2.set_ylabel('Measured Speedup Factor (X Faster)', fontsize=11, fontweight='bold')
ax2.set_title('Buffering Acceleration Factor (vs Unbuffered)', fontsize=12, fontweight='bold')
ax2.grid(True, ls="--", alpha=0.5)

for bar in bars:
    yval = bar.get_height()
    ax2.text(bar.get_x() + bar.get_width()/2.0, yval + 5, f"{yval:.1f}x", ha='center', va='bottom', fontweight='bold', color='#4a148c')

plt.suptitle('TASK B1.3: Empirical Buffering Benchmark (Student: Dhanush - 192311094)', fontsize=14, fontweight='bold', y=1.02)
plt.tight_layout()
fig.savefig(f"{evidence_dir}/chart1_buffering_comparison.png", bbox_inches='tight')
plt.close(fig)
print("Chart 1 generated successfully!")

# --------------------------------------------------------------------------------------------------
# CHART 2: Search Time Growth (Linear Scan vs In-Memory HashMap)
# --------------------------------------------------------------------------------------------------
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5.5), dpi=fig_dpi)

counts = ['1,000', '10,000', '100,000']
head_ms = [6.699, 12.719, 49.696]
mid_ms = [2.162, 7.632, 22.655]
tail_ms = [2.236, 13.753, 44.342]
notfound_ms = [2.086, 11.310, 32.435]

ax1.plot(counts, head_ms, marker='o', linewidth=2.2, label='Head Search', color='#3498db')
ax1.plot(counts, mid_ms, marker='s', linewidth=2.2, label='Mid Search', color='#f39c12')
ax1.plot(counts, tail_ms, marker='^', linewidth=2.2, label='Tail Search', color='#e74c3c')
ax1.plot(counts, notfound_ms, marker='d', linewidth=2.2, label='Not Found (Worst Case)', color='#9b59b6')

ax1.set_ylabel('Linear Scan Search Time (ms)', fontsize=11, fontweight='bold')
ax1.set_xlabel('Record Count in Store File', fontsize=11, fontweight='bold')
ax1.set_title('Linear File Scan Search Latency Growth (O(N))', fontsize=12, fontweight='bold')
ax1.legend(frameon=True)
ax1.grid(True, ls="--", alpha=0.5)

# Comparison: 100k Linear vs HashMap
modes = ['Linear Tail (100k)', 'Linear Not Found', 'HashMap Load (Once)', 'HashMap Lookup']
latencies_ms = [44.342, 32.435, 59.95, 0.0087]
colors = ['#e74c3c', '#9b59b6', '#34495e', '#2ecc71']

bars2 = ax2.bar(modes, latencies_ms, color=colors, width=0.5)
ax2.set_ylabel('Execution Time (ms) - Log Scale', fontsize=11, fontweight='bold')
ax2.set_title('Alternative Concept: Linear Scan vs HashMap (100,000 Records)', fontsize=12, fontweight='bold')
ax2.set_yscale('log')
ax2.grid(True, which="both", ls="--", alpha=0.5)
plt.setp(ax2.get_xticklabels(), rotation=15, ha='right')

for bar in bars2:
    yval = bar.get_height()
    label = f"{yval:.4f} ms" if yval < 1 else f"{yval:.1f} ms"
    ax2.text(bar.get_x() + bar.get_width()/2.0, yval * 1.25, label, ha='center', va='bottom', fontweight='bold', fontsize=9)

plt.suptitle('TASK B1.4: Search Time Growth & In-Memory Indexing Evaluation', fontsize=14, fontweight='bold', y=1.02)
plt.tight_layout()
fig.savefig(f"{evidence_dir}/chart2_search_growth.png", bbox_inches='tight')
plt.close(fig)
print("Chart 2 generated successfully!")

# --------------------------------------------------------------------------------------------------
# CHART 3: JDBC Insertion Benchmark (Statement vs PreparedStatement vs Batch)
# --------------------------------------------------------------------------------------------------
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5.5), dpi=fig_dpi)

sizes_jdbc = ['1,000 Rows', '10,000 Rows', '100,000 Rows']
stmt_ms = [3362, 24006, 280000]
ps_ms = [3048, 24079, 220000]
batch_ms = [82, 398, 2989]

x_jdbc = np.arange(len(sizes_jdbc))
w_jdbc = 0.25

r1 = ax1.bar(x_jdbc - w_jdbc, stmt_ms, w_jdbc, label='Statement.executeUpdate()', color='#e74c3c')
r2 = ax1.bar(x_jdbc, ps_ms, w_jdbc, label='PreparedStatement (unbatched)', color='#f39c12')
r3 = ax1.bar(x_jdbc + w_jdbc, batch_ms, w_jdbc, label='PreparedStatement BATCH (1,000 rows)', color='#27ae60')

ax1.set_ylabel('Total Insertion Time (ms) - Log Scale', fontsize=11, fontweight='bold')
ax1.set_title('Insertion Latency by Pattern (Log Scale)', fontsize=12, fontweight='bold')
ax1.set_xticks(x_jdbc)
ax1.set_xticklabels(sizes_jdbc, fontweight='bold')
ax1.set_yscale('log')
ax1.legend(frameon=True)
ax1.grid(True, which="both", ls="--", alpha=0.5)

# Throughput chart (rows/sec)
tp_stmt = [297, 416, 357]
tp_ps = [328, 415, 454]
tp_batch = [12195, 25125, 33456]

r1_tp = ax2.bar(x_jdbc - w_jdbc, tp_stmt, w_jdbc, label='Statement', color='#e74c3c')
r2_tp = ax2.bar(x_jdbc, tp_ps, w_jdbc, label='PreparedStatement', color='#f39c12')
r3_tp = ax2.bar(x_jdbc + w_jdbc, tp_batch, w_jdbc, label='Batch (PassLoader)', color='#27ae60')

ax2.set_ylabel('Throughput (Rows Inserted per Second)', fontsize=11, fontweight='bold')
ax2.set_title('Ingestion Throughput (Rows/Second)', fontsize=12, fontweight='bold')
ax2.set_xticks(x_jdbc)
ax2.set_xticklabels(sizes_jdbc, fontweight='bold')
ax2.legend(frameon=True)
ax2.grid(True, ls="--", alpha=0.5)

for bar in r3_tp:
    yval = bar.get_height()
    ax2.text(bar.get_x() + bar.get_width()/2.0, yval + 800, f"{int(yval):,} r/s", ha='center', va='bottom', fontweight='bold', color='#145a32', fontsize=9)

plt.suptitle('TASK B3: JDBC Persistence Engineering Performance Comparison', fontsize=14, fontweight='bold', y=1.02)
plt.tight_layout()
fig.savefig(f"{evidence_dir}/chart3_jdbc_performance.png", bbox_inches='tight')
plt.close(fig)
print("Chart 3 generated successfully!")

# --------------------------------------------------------------------------------------------------
# CHART 4: Transaction Atomicity & Failure Resilience
# --------------------------------------------------------------------------------------------------
fig, ax = plt.subplots(figsize=(8, 4.5), dpi=fig_dpi)

categories = ['Scenario A: Auto-Commit ON\n(Crash at Record 500)', 'Scenario B: Single Transaction\n(Crash at Record 500)']
remaining_rows = [500, 0]
colors_atom = ['#c0392b', '#27ae60']

bars_atom = ax.bar(categories, remaining_rows, color=colors_atom, width=0.45)
ax.set_ylabel('Remaining Records in Database Table', fontsize=11, fontweight='bold')
ax.set_title('TASK B3.3: Database State After Forced Mid-Stream Failure', fontsize=13, fontweight='bold')
ax.set_ylim(0, 600)
ax.grid(True, ls="--", alpha=0.5)

ax.text(bars_atom[0].get_x() + bars_atom[0].get_width()/2.0, 515, '500 Records Orphaned!\n(Data Corrupted)', ha='center', va='bottom', fontweight='bold', color='#900c3f')
ax.text(bars_atom[1].get_x() + bars_atom[1].get_width()/2.0, 15, '0 Records\n(Pristine State via con.rollback())', ha='center', va='bottom', fontweight='bold', color='#145a32')

plt.tight_layout()
fig.savefig(f"{evidence_dir}/chart4_transaction_atomicity.png", bbox_inches='tight')
plt.close(fig)
print("Chart 4 generated successfully!")
